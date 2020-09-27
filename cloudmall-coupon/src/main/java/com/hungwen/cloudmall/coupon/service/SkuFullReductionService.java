package com.hungwen.cloudmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.common.to.SkuReductionTo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品滿減資訊
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:40:00
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo reductionTo);

}

