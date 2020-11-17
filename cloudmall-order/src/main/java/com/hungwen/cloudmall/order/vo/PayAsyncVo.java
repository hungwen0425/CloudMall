package com.hungwen.cloudmall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
public class PayAsyncVo {

    private String gmt_create;
    private String charset;
    private String gmt_payment;
    private Date notify_time;
    private String subject;
    private String sign;
    // 支付者的 id
    private String buyer_id;
    // 訂單的資料
    private String body;
    // 支付金額
    private String invoice_amount;
    private String version;
    // 通知 id
    private String notify_id;
    private String fund_bill_list;
    //通知類型； trade_status_sync
    private String notify_type;
    // 訂單號
    private String out_trade_no;
    // 支付的總額
    private String total_amount;
    // 交易狀態  TRADE_SUCCESS
    private String trade_status;
    // 流水號
    private String trade_no;
    private String auth_app_id;
    // 商家收到的款
    private String receipt_amount;
    private String point_amount;
    // 應用 id
    private String app_id;
    // 最終支付的金額
    private String buyer_pay_amount;
    // 簽名類型
    private String sign_type;
    // 商家的id
    private String seller_id;

}
