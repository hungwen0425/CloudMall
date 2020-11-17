package com.hungwen.cloudmall.order.vo;

import lombok.Data;

/**
 * @Description: 庫存 vo
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen  Tseng
 * @createTime: 2020-11-09 18:13
 **/

@Data
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;

}
