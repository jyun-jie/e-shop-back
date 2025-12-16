package com.shop.service.serviceImpl;

import com.shop.component.NewebPayClient;
import com.shop.entity.MasterOrder;
import com.shop.entity.Payment;
import com.shop.mapper.MasterOrderMapper;
import com.shop.mapper.PaymentMapper;
import com.shop.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private MasterOrderMapper masterMapper;
    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private NewebPayClient newebPayClient;

    public String createPayment(int masterOrderId) {


        MasterOrder master = masterMapper.findById(masterOrderId);

        String tradeNo = "NP" + System.currentTimeMillis();

        Payment payment = new Payment();
        payment.setMaster_order_id(masterOrderId);
        payment.setTrade_no(tradeNo);
        payment.setAmount(master.getTotal_amount());
        payment.setPay_status("INIT");

        paymentMapper.insert(payment);

        return newebPayClient.buildPayForm(tradeNo, payment.getAmount());
    }

    @Transactional
    public void handleNewebPayCallback(Map<String, String> data) {

        String tradeInfo = data.get("TradeInfo");
        String tradeSha = data.get("TradeSha");

        // 防呆
        if (tradeInfo == null || tradeSha == null) {
            throw new IllegalArgumentException("缺少 TradeInfo 或 TradeSha");
        }

        // 2️⃣ 驗 TradeSha（超重要）
        boolean valid = newebPayClient.verifyTradeSha(tradeInfo, tradeSha);
        if (!valid) {
            throw new IllegalStateException("TradeSha 驗證失敗");
        }

        // 3️⃣ 解密 TradeInfo（這一步才會得到 JSON）
        String jsonStr = newebPayClient.decryptTradeInfo(tradeInfo);
        Map<String, String> resultMap =newebPayClient.parseQueryString(jsonStr);
        JSONObject result = new JSONObject(resultMap);


        if (!"SUCCESS".equals(result.getString("Status"))) {
            String message = result.getString("Message");
            System.out.println("交易狀態失敗: " + message);
            return;
        }

        String tradeNo =
                result.getString("MerchantOrderNo");

        Payment payment = paymentMapper.findByTradeNo(tradeNo);

        if (payment == null) {
            throw new IllegalStateException("找不到對應 Payment: " + tradeNo);
        }

        // 冪等保護（非常重要）
        if ("PAID".equals(payment.getPay_status())) {
            return;
        }

        paymentMapper.updateStatus(payment.getId(), "PAID");
        masterMapper.updateStatus(payment.getMaster_order_id(), "PAID");
    }

}
