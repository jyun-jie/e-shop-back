package com.shop.component;

import com.shop.util.AesUtil;
import com.shop.util.ShaUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NewebPayClient {
    @Value("${newebpay.merchant-id}")
    private String merchantId;

    @Value("${newebpay.hash-key}")
    private String hashKey;

    @Value("${newebpay.hash-iv}")
    private String hashIv;

    @Value("${newebpay.pay-url}")
    private String payUrl;

    @Value("${newebpay.version}")
    private String version;

    public String buildPayForm(String tradeNo, int amount) {

        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", merchantId);
        params.put("RespondType", "String");
        params.put("TimeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("Version", version);
        params.put("MerchantOrderNo", tradeNo);
        params.put("Amt", String.valueOf(amount));
        params.put("ItemDesc", "商城訂單");
        params.put("NotifyURL", "https://proleptical-unfastidiously-krissy.ngrok-free.dev/Api/Payment/notify");
        params.put("ReturnURL", "https://proleptical-unfastidiously-krissy.ngrok-free.dev/Read");

        String aes = encryptAES(params);
        String sha = encryptSHA(aes);

        return """
        <form method="POST" action="https://ccore.newebpay.com/MPG/mpg_gateway">
            <input type="hidden" name="MerchantID" value="%s"/>
            <input type="hidden" name="TradeInfo" value="%s"/>
            <input type="hidden" name="TradeSha" value="%s"/>
            <input type="hidden" name="Version" value="2.3"/>
            <button type="submit">前往付款</button>
        </form>
    """.formatted(merchantId, aes, sha);
    }

    private String encryptAES(Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        return AesUtil.encrypt(query, hashKey, hashIv);
    }

    private String encryptSHA(String tradeInfo) {
        return ShaUtil.sha256(
                "HashKey=" + hashKey + "&" + tradeInfo + "&HashIV=" + hashIv
        );
    }

    public boolean verifyTradeSha(String tradeInfo, String tradeSha) {
        String raw = "HashKey=" + hashKey
                + "&" + tradeInfo
                + "&HashIV=" + hashIv;

        String sha = DigestUtils.sha256Hex(raw).toUpperCase();
        return sha.equals(tradeSha);
    }

    public String decryptTradeInfo(String tradeInfo) {
        return AesUtil.decrypt(tradeInfo, hashKey, hashIv);
    }

    public Map<String, String> parseQueryString(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return map;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            // 確保 Key 和 Value 都存在
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                // 建議進行 URL 解碼，雖然這個範例中可能不需要
                // map.put(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
                map.put(key, value);
            }
        }
        return map;
    }
}
