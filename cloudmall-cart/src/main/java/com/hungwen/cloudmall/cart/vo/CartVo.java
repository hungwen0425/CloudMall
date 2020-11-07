package com.hungwen.cloudmall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 整個購物車
 * 需要算地屬性，必須重寫它的 get 方法，保證每次查詢屬性都會進行計算
 */
@Data
public class CartVo {
    List<CartItemVo> items;
    private Integer countNum; //商品數量
    private Integer countType; //商品類型數量
    private BigDecimal totalAmount; //商品總價
    private BigDecimal reduce = new BigDecimal("0"); //減免價格

    public List<CartItemVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if(items != null && items.size() > 0){
            for (CartItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if(items != null && items.size() > 0){
            for (CartItemVo item : items) {
                count += 1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        // 1. 計算購物項總價
        if(items != null && items.size() > 0){
            for (CartItemVo item : items) {
                if(item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }
        // 2. 減去優惠總價
        BigDecimal subtract = amount.subtract(getReduce());

        return subtract;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
