package com.hungwen.cloudmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.cloudmall.product.vo.AttrGroupWithAttrsVo;
import com.hungwen.cloudmall.product.vo.SpuItemAttrGroupVo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 屬性分組
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long catalogId, Long spuId);
}

