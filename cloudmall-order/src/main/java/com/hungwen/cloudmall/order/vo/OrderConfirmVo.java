package com.hungwen.cloudmall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订單確認頁需要用的資料
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-09 18:59
 **/
public class OrderConfirmVo {
    //  會員收獲地址列表
    @Setter @Getter
    List<MemberAddressVo> address;
    // 所有選中的購物項
    @Setter @Getter
    List<OrderItemVo> items;
    // 發票記錄

    // 優惠券（會員積分）
    @Setter @Getter
    private Integer integration;
    // 訂單總額
    // BigDecimal total;
    // 應付價格
    // BigDecimal payPrice;
    // 防止重復提交的令牌
    @Setter @Getter
    private String orderToken;
    @Setter @Getter
    Map<Long, Boolean> stocks;

    public Integer getCount() {
        Integer count = 0;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }
    // 計算訂單總額
    public BigDecimal getTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                // 計算當前商品的總價格
                BigDecimal itemPrice = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                // 再計算全部商品的總價格
                sum = sum.add(itemPrice);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
