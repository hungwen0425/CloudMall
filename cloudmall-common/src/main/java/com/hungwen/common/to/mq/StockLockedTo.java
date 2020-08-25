package com.hungwen.common.to.mq;

import lombok.Data;


/**
 * @Description TODO
 * @Author Hungwen Tseng
 * @Date 2020/6/16 18:17
 * @Version 1.0
 **/
@Data
public class StockLockedTo {

    private Long id; // 庫存工作單 id

    private StockDetailTo stockDetail; // 工作單詳情 id
}
