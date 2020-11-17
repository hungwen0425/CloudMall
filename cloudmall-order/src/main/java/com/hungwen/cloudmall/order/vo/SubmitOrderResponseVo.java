package com.hungwen.cloudmall.order.vo;

import com.hungwen.cloudmall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-09 22:34
 **/
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    // 錯誤狀態碼
    private Integer code;
}
