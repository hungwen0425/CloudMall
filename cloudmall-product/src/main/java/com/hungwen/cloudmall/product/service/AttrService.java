package com.hungwen.cloudmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.cloudmall.product.vo.AttrGroupRelationVo;
import com.hungwen.cloudmall.product.vo.AttrRespVo;
import com.hungwen.cloudmall.product.vo.AttrVo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品屬性
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attrVo);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}

