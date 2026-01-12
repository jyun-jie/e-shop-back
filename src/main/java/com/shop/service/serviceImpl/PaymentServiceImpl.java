package com.shop.service.serviceImpl;

import com.shop.component.NewebPayClient;
import com.shop.entity.MasterOrder;
import com.shop.entity.OrderState;
import com.shop.entity.Payment;
import com.shop.mapper.BuyerOrderMapper;
import com.shop.mapper.MasterOrderMapper;
import com.shop.mapper.PaymentMapper;
import com.shop.service.PaymentService;
import com.shop.service.payment.factory.PaymentStrategyFactory;
import com.shop.service.payment.strategy.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private MasterOrderMapper masterMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private BuyerOrderMapper buyerOrderMapper; // 注入 BuyerOrderMapper

    @Autowired
    private NewebPayClient newebPayClient;

    @Autowired
    private PaymentStrategyFactory strategyFactory;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createPayment(int masterOrderId) {


        MasterOrder master = masterMapper.findById(masterOrderId);

        PaymentStrategy  strategy = strategyFactory.getStrategy(master.getPay_method().toString())  ;

        return strategy.createPayment(master) ;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleNewebPayCallback(Map<String, String> data) {

        String tradeInfo = data.get("TradeInfo");
        String tradeSha = data.get("TradeSha");

        // 防呆
        if (tradeInfo == null || tradeSha == null) {
            throw new IllegalArgumentException("缺少 TradeInfo 或 TradeSha");
        }

        // 驗 TradeSha
        if (!newebPayClient.verifyTradeSha(tradeInfo, tradeSha)) {
            throw new IllegalStateException("TradeSha 驗證失敗，疑似偽造請求");
        }

        // 3️⃣ 解密 TradeInfo（這一步才會得到 JSON）
        String jsonStr = newebPayClient.decryptTradeInfo(tradeInfo);
        Map<String, String> resultMap =newebPayClient.parseQueryString(jsonStr);
        JSONObject result = new JSONObject(resultMap);




        String tradeNo = result.getString("MerchantOrderNo");
        Payment payment = paymentMapper.findByTradeNoForUpdate(tradeNo);

        if (payment == null) {
            throw new IllegalStateException("找不到對應 Payment: " + tradeNo);
        }

        // 冪等保護（非常重要）
        if ("PAID".equals(payment.getPay_status())) {
            log.info("訂單 {} 已處理過，忽略此次通知", tradeNo);
            return;
        }

        if ("SUCCESS".equals(result.getString("Status"))) {
            paymentMapper.updateStatus(payment.getId(), "PAID");
            masterMapper.updateStatus(payment.getMaster_order_id(), "PAID");
            
            // [修正點] 連動更新子訂單狀態為 Not_Ship (待出貨)
            buyerOrderMapper.updateStateByMasterOrderId(payment.getMaster_order_id(), OrderState.Not_Ship);
            
        }else{
            paymentMapper.updateStatus(payment.getId(), "FAILED");
            log.error("藍新支付失敗，訂單號: {}, 原因: {}", tradeNo, result.getString("Message"));

        }
    }

    @Override
    public String queryTradeInfo(Map<String,String> data) {
        return newebPayClient.buildQueryTradeInfo(data);
    }


    /***
     *  模擬COD 收到錢的流程
     *
     */
    @Override
    public void changePayStatus(String tradeNo) {
        Payment payment = paymentMapper.findByTradeNoForUpdate(tradeNo) ;

        paymentMapper.updateStatus(payment.getId(), "PAID");
        masterMapper.updateStatus(payment.getMaster_order_id(), "PAID");
        
        // [修正點] 模擬也需要連動更新
        buyerOrderMapper.updateStateByMasterOrderId(payment.getMaster_order_id(), OrderState.Not_Ship);
    }
}
