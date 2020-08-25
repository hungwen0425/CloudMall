package com.hungwen.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description TODO
 * @Author Hunngwen Tseng
 * @Date 2020/6/16 18:15
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
     * 秒殺價格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒殺總量
     */
    private Integer num;
    /**
     * 會員 id
     */
    private Long memberId;
}
