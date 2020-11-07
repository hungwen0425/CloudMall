package com.hungwen.cloudmall.product.app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hungwen.cloudmall.product.entity.SkuInfoEntity;
import com.hungwen.cloudmall.product.service.SkuInfoService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.R;



/**
 * sku資料
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 根據 skuId 查詢當前商品的最新價格
     * @param skuId
     * @return
     */
    @GetMapping(value = "/{skuId}/price")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId) {
        // 查詢當前商品的資料
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        // 查詢商品的價格
        BigDecimal price = skuInfo.getPrice();
        return price;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }

    /**
     * 資料
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));
        return R.ok();
    }

}
