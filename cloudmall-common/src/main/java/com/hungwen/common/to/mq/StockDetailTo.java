package com.hungwen.common.to.mq;

import lombok.Data;

/**
 * @Description TODO
 * @Author Hungwen Tseng
 * @Date 2020/5/6 20:09
 * @Version 1.0
 **/
@Data
public class StockDetailTo {
    /**
     * id
     */
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 購買個數
     */
    private Integer skuNum;
    /**
     * 工作單 id
     */
    private Long taskId;
    /**
     * 倉庫 id
     */
    private Long wareId;
    /**
     * 庫存鎖定狀態（1：已鎖定 2：已解鎖 3：扣減了庫存）
     */
    private Integer lockStatus;
}
