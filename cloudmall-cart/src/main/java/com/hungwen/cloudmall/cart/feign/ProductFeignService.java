package com.hungwen.cloudmall.cart.feign;

import com.hungwen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-05 20:36
 **/

@FeignClient("cloudmall-product")
public interface ProductFeignService {

    /**
     * 根據 skuId 查詢 sku 資料
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getInfo(@PathVariable("skuId") Long skuId);

    /**
     * 根據 skuId 查詢 pms_sku_sale_attr_value 表中的資料
     * @param skuId
     * @return
     */
    @GetMapping(value = "/product/skusaleattrvalue/stringList/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    /**
     * 根據 skuId 查詢當前商品的最新價格
     * @param skuId
     * @return
     */
    @GetMapping(value = "/product/skuinfo/{skuId}/price")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId);
}
