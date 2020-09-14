package com.hungwen.cloudmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三級分類主檔
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);
    /**
     * 找出 catelogId 的完整路徑
     * [父路徑 / 子路徑 / 孫路徑]
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(long catelogId);

    void updateCascade(CategoryEntity category);
}

