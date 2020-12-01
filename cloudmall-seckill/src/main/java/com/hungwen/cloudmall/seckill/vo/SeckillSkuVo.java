package com.hungwen.cloudmall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-29 21:13
 **/

@Data
public class SeckillSkuVo {

    private Long id;
    /**
     * 活動id
     */
    private Long promotionId;
    /**
     * 活動場次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 限時搶購價格
     */
    private BigDecimal seckillPrice;
    /**
     * 限時搶購總量
     */
    private Integer seckillCount;
    /**
     * 每人限購數量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

}
