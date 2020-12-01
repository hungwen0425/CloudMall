package com.hungwen.cloudmall.order.listener;

import com.hungwen.cloudmall.order.config.LinepayTemplate;
import com.hungwen.cloudmall.order.service.OrderService;
import com.hungwen.cloudmall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 訂單支付成功監聽器
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-18 17:39
 **/

@RestController
public class OrderPayedListener {

    @Autowired
    private OrderService orderService;
    @Autowired
    private LinepayTemplate linepayTemplate;

    @PostMapping(value = "/payed/notify")
    public String handleAlipayed(PayAsyncVo asyncVo, HttpServletRequest request) {
        // 只要收到支付寶的異步通知，返回 success 支付寶便不再通知
        // 查詢支付寶 POST 過來反饋資料
        //TODO 需要驗簽
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            // 亂碼解決，這段代碼在出現亂碼時使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return null;
    }

    @PostMapping(value = "/pay/notify")
    public String asyncNotify(@RequestBody String notifyData) {
        // 異步通知結果
        return orderService.asyncNotify(notifyData);
    }
}
