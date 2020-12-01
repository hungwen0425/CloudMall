package com.hungwen.cloudmall.seckill.to;

import com.hungwen.cloudmall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 給Redis中存放的skuInfo的資料
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSeng
 * @createTime: 2020-11-29 21:39
 **/

@Data
public class SeckillSkuRedisTo {

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

    // sku 的詳細資料
    private SkuInfoVo skuInfo;

    // 當前商品限時搶購的開始時間
    private Long startTime;

    // 當前商品限時搶購的結束時間
    private Long endTime;

    // 當前商品限時搶購的隨機碼
    private String randomCode;
}
