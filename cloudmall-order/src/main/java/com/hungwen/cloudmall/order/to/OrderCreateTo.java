package com.hungwen.cloudmall.order.to;

import com.hungwen.cloudmall.order.entity.OrderEntity;
import com.hungwen.cloudmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 訂單 to
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-09 23:04
 **/
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    // 訂單計算的應付價格
    private BigDecimal payPrice;
    // 運費
    private BigDecimal fare;
}
