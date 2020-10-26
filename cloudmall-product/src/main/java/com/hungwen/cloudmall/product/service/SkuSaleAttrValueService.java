package com.hungwen.cloudmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.cloudmall.product.vo.SkuItemSaleAttrVo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku銷售屬性&值
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);

}

