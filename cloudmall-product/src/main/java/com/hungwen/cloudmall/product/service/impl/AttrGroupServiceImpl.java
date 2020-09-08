package com.hungwen.cloudmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hungwen.cloudmall.product.entity.AttrEntity;
import com.hungwen.cloudmall.product.service.AttrService;
import com.hungwen.cloudmall.product.vo.AttrGroupWithAttrsVo;
import com.hungwen.cloudmall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.product.dao.AttrGroupDao;
import com.hungwen.cloudmall.product.entity.AttrGroupEntity;
import com.hungwen.cloudmall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        // select * from pms_attr_group where catelog_id=? and (attr_group_id=key or attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(key)) {
            wrapper.and((obj) -> obj.eq("attr_group_id", key).or().like("attr_group_name", key));
        }

        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        } else {
            // select * from pms_attr_group where catelog_id=? and (attr_group_id=key or attr_group_name like %key%)
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根據分類 id 查出所有屬性分組以及這些組裡面的屬性
     * @Param catelogId
     * @return List<AttrGroupWithAttrsVo>
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1、查詢分組登入
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //2、查詢所有屬性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(attrGroup -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroup, attrsVo);
            //Todo List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            //Todo attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long catalogId, Long spuId) {
        //1、由 catalogId 查詢表 `pms_attr_group` 的 attr_group_id
        //2. 再由 attr_group_id 查詢關聯表 `pms_attr_attrgroup_relation` 取得 attr_id
        //3. 再由 spuId 與 attr_id 查詢表 pms_product_attr_value` 查出對應的所有屬性 attr_value
        /**
         *        select
         *          pav.`spu_id`,
         *          ag.attr_group_id,
         *          ag.attr_group_name,
         *          aar.attr_id,
         *          pav.attr_name,
         *          pav.attr_value
         *         from pms_attr_group ag
         *                left join pms_attr_attrgroup_relation aar on aar.attr_group_id = ag.attr_group_id
         *                left join pms_product_attr_value pav on pav.attr_id = aar.attr_id
         *         where catelog_id = 225 and spu_id = 7
         */
        return this.baseMapper.getAttrGroupWithAttrsBySpuId(catalogId, spuId);
    }

}