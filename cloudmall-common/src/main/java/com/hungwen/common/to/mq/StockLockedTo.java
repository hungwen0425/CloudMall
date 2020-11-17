package com.hungwen.common.to.mq;

import lombok.Data;


/**
 * @Description
 * @Author Hungwen Tseng
 * @Date 2020/6/16 18:17
 * @Version 1.0
 **/
@Data
public class StockLockedTo {
    // 庫存工作單 id
    private Long id;
    // 工作單詳情 id
    private StockDetailTo stockDetail;
}
