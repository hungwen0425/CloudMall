package com.hungwen.cloudmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.hungwen.cloudmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {
    // 應用 ID，您的 APPID，收款賬號既是您的 APPID 對應支付寶賬號
    public String app_id;
    // 商戶私鑰，您的 PKCS8 格式 RSA2 私鑰
    public String merchant_private_key;
    // 支付寶公鑰，查看地址：https://openhome.alipay.com/platform/keyManage.htm 對應 APPID 下的支付寶公鑰。
    public String alipay_public_key;
    // 服務器 [異步通知] 頁面路徑需 http:// 格式的完整路徑，不能加 ?id=123 這類自定義參數，必須外網可以正常訪問
    // 支付寶會悄悄的給我們發送一個請求，告訴我們支付成功的 資料
    public String notify_url;
    // 頁面跳轉同步通知頁面路徑 需http://格式的完整路徑，不能加?id=123這類自定義參數，必須外網可以正常訪問
    // 同步通知，支付成功，一般跳轉到成功頁
    public String return_url;
    // 簽名方式
    private  String sign_type;
    // 字元編碼格式
    private  String charset;
    // 訂單超時時間
    private String timeout = "1m";
    // 支付寶網關； https://openapi.alipaydev.com/gateway.do
    public String gatewayUrl;

    public  String pay(PayVo vo) throws AlipayApiException {
        // AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        // 1. 根據支付寶的配置生成一個支付客戶端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);
        // 2. 創建一個支付請求 //設置請求參數
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);
        //商戶訂單號，商戶網站訂單系統中唯一訂單號，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金額，必填
        String total_amount = vo.getTotal_amount();
        //訂單名稱，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();
        // 會收到支付寶的響應，響應的是一個頁面，只要瀏覽器顯示這個頁面，就會自動來到支付寶的收銀台頁面
        System.out.println("支付寶的響應：" + result);
        return result;
    }
}
