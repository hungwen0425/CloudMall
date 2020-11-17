package com.hungwen.cloudmall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 鎖定庫存的 vo
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSEng
 * @createTime: 2020-07-05 10:52
 **/

@Data
public class WareSkuLockVo {
    private String orderSn;
    // 需要鎖住的所有庫存 資料
    private List<OrderItemVo> locks;
}
