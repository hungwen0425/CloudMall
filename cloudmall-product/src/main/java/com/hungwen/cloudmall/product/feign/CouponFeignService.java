package com.hungwen.cloudmall.product.feign;

import com.hungwen.common.to.SkuReductionTo;
import com.hungwen.common.to.SpuBoundTo;
import com.hungwen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author : Hungwen Tseng
 * @date : 2020-09-27
 */
@FeignClient("cloudmall-coupon")
public interface CouponFeignService {

    /**
     * 1. CouponFeignService.saveSpuBounds(spuBoundTo);
     *    1.1 @RequestBody 將這個物件轉為 json。
     *    1.2 找到 cloudmall-coupon 服務，給 /coupon/spubounds/save 發送請求。
     *        將上一步轉的 json 放在請求體位置，發送請求；
     *    1.3 對方服務收到請求。請求體裡面有 json 資料。
     *       (@RequestBody SpuBoundsEntity spuBounds)；將請求體的 json 轉為 SpuBoundsEntity；
     * 只要 json 資料模型是兼容的。雙方服務無需使用同一個 to
     *
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    /**
     * @param skuReductionTo
     * @return
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
