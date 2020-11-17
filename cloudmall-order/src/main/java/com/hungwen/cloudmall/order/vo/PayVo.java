package com.hungwen.cloudmall.order.vo;

import lombok.Data;

@Data
public class PayVo {
    // 商戶訂單號 必填
    private String out_trade_no;
    // 訂單名稱 必填
    private String subject;
    // 付款金額 必填
    private String total_amount;
    // 商品描述 可空
    private String body;
}
