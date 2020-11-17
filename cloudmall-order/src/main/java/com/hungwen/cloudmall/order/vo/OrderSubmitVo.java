package com.hungwen.cloudmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 訂單提交資料的 vo
 * @Created: with IntelliJ IDEA.
 * @author:  Hungwen TSeng
 * @createTime: 2020-11-09 11:54
 **/

@Data
public class OrderSubmitVo {
    // 收貨地址的 id
    private Long addrId;
    // 支付方式
    private Integer payType;
    // 無需提交要購買的商品，去購物車再查詢一遍
    // 優惠、發票
    // 防止重複提交的令牌
    private String orderToken;
    // 應付價格
    private BigDecimal payPrice;
    // 訂單備註
    private String remarks;
    // 用戶相關的資料，直接去 session 中取出即可
}
