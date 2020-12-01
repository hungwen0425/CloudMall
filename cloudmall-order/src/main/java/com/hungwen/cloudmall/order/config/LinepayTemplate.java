package com.hungwen.cloudmall.order.config;


import com.hungwen.cloudmall.order.vo.PayVo;
import lombok.Data;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "linepay")
@Component
@Data
public class LinepayTemplate {
    // CHANNEL ID，收款賬號既是您的 CHANNELID 對應 LINE Pay賬號
    public String channel_id;
    // CHANNEL SECERET，商戶私鑰，您的 RSA2 私鑰
    public String channel_secret;
    // 我們發送一個請求給 LINE Pay
    public String payment_url;
    // 頁面跳轉同步通知頁面路徑 需 http:// 格式的完整路徑，不能加 ?id=123 這類自定義參數，必須外網可以正常訪問
    // 同步通知，支付成功，一般跳轉到成功頁
    public String confirm_url;
    // 幣別
    private  String currency;
    // 字元編碼格式
    private  String charset;

    public  String pay(PayVo vo) throws IOException {
        //商戶訂單號，商戶網站訂單系統中唯一訂單號，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金額，必填
        String total_amount = vo.getTotal_amount();
        //訂單名稱，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        HttpClient httpclient = new DefaultHttpClient();
        String postRequest = "POST format";
        // 建立 HttpPost
        HttpPost request  = new HttpPost(payment_url);
        request.setHeader("Content-Type", charset);
        request.setHeader("X-LINE-ChannelId", channel_id);
        request.setHeader("X-LINE-ChannelSecret", channel_secret);
        // Request body - json
        String json = "{\"productName\":\"" + subject + "\","
                + "\"amount\":\"" + total_amount + "\","
                + "\"currency\":\""+ currency +"\","
                + "\"orderId\":\"" + out_trade_no + "\","
                + "\"confirmUrl\":\"" + confirm_url + "\"}";
        StringEntity reqEntity = new StringEntity(json);
        request.setEntity(reqEntity);
        HttpResponse response = httpclient.execute(request);
        String result = response.toString();
        // 會收到 LINE Pay 的響應，響應的是一個頁面，只要瀏覽器顯示這個頁面，就會自動來到支付寶的收銀台頁面
        System.out.println("LINE Pay的響應：" + result);
        return result;
    }
}
