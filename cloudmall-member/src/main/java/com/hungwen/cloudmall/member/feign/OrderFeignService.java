package com.hungwen.cloudmall.member.feign;

import com.hungwen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen
 * @createTime: 2020-11-25 15:34
 **/
@FeignClient("cloudmall-order")
public interface OrderFeignService {

    /**
     * 分頁查詢當前登入用戶的所有訂單資料
     * @param params
     * @return
     */
    @PostMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);

}
