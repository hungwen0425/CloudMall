package com.hungwen.cloudmall.order.feign;

import com.hungwen.cloudmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: HungwenTseng
 * @createTime: 2020-11-09 20:11
 **/

@FeignClient("cloudmall-cart")
public interface CartFeignService {

    /**
     * 查詢當前用戶購物車選中的商品項
     * @return
     */
    @GetMapping(value = "/getCurrentCartItems")
    List<OrderItemVo> getCurrentCartItems();
}
