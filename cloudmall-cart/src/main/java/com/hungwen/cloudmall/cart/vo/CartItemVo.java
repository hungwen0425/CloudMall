package com.hungwen.cloudmall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 購物項內容
 */
@Data
public class CartItemVo {
    // skuId
    private Long skuId;
    // 是否選中
    private Boolean check = true;
    // 標題
    private String title;
    // 圖片
    private String image;
    // 商品套餐屬性
    private List<String> skuAttrValues;
    // 價格
    private BigDecimal price;
    // 數量
    private Integer count;
    // 總價
    private BigDecimal totalPrice;

    /**
     * 當前購物車項總價等於單價x數量
     *
     * @return
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}