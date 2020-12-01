package com.hungwen.cloudmall.seckill.feign;

import com.hungwen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSeng
 * @createTime: 2020-11-29 19:33
 **/

@FeignClient("cloudmall-coupon")
public interface CouponFeignService {

    /**
     * 查詢最近三天需要參加限時搶購商品的資料
     * @return
     */
    @GetMapping(value = "/coupon/seckillsession/Lates3DaySession")
    R getLates3DaySession();

}
