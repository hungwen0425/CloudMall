package com.hungwen.cloudmall.product.feign;

import com.hungwen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-10 15:53
 **/

//@FeignClient(value = "cloudmall-seckill",fallback = SeckillFeignServiceFallBack.class)
//public interface SeckillFeignService {
//
//    /**
//     * 根據skuId查詢商品是否参加秒殺活動
//     * @param skuId
//     * @return
//     */
//    @GetMapping(value = "/sku/seckill/{skuId}")
//    R getSkuSeckilInfo(@PathVariable("skuId") Long skuId);
//
//}
