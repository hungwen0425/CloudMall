package com.hungwen.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author Hunngwen Tseng
 * @Date 2020/11/11 18:15
 * @Version 1.0
 **/
@Data
public class SecKillOrderTo {
    /**
     * 訂單編號
     */
    private String orderSn;
    /**
     * 活動場次 id
     */
    private Long promotionSessionId;
    /**
     * 商品 id
     */
    private Long skuId;
    /**
     * 限時搶購價格
     */
    private BigDecimal seckillPrice;
    /**
     * 限時搶購總量
     */
    private Integer num;
    /**
     * 會員 id
     */
    private Long memberId;
}
