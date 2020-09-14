package com.hungwen.cloudmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hungwen.cloudmall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.product.dao.BrandDao;
import com.hungwen.cloudmall.product.entity.BrandEntity;
import com.hungwen.cloudmall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //取得 key
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> brandEntityQueryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(key)) {
            brandEntityQueryWrapper.eq("brand_id", key).or().like("name", key);
        }

        IPage<BrandEntity> page = this.page(new Query<BrandEntity>().getPage(params), brandEntityQueryWrapper);
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updateDetail(BrandEntity brand) {
        //保證冗余字段的備註一致
        this.updateById(brand);
        if (!org.springframework.util.StringUtils.isEmpty(brand.getName())) {
            //同步更新其他關聯表中的備註
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
            //TODO 更新其他關聯
        }
    }

}