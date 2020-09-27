package com.hungwen.cloudmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hungwen.cloudmall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.product.dao.CategoryDao;
import com.hungwen.cloudmall.product.entity.CategoryEntity;
import com.hungwen.cloudmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1. 查出所有分類
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2. 組裝成父子的樹型結構
        //2.1 找到所有一級分類
        List<CategoryEntity> level1Menu = entities.stream().filter(
                categoryEntities -> categoryEntities.getParentCid() == 0
        ).map((menu)->{
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2)->{
            //選單的排序
            return (menu1.getSort() == null ? 0:menu1.getSort()) - (menu2.getSort() == null ? 0:menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menu;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1. 檢查當前刪除的選單，是否被別的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    // [2, 25, 225]
    @Override
    public Long[] findCatelogPath(long catelogId) {
        List<Long> paths = Lists.newArrayList();
        List<Long> parentPath = findParentPath(catelogId, paths);
        // 因為收集的鎮列為 [225, 25, 2]，所以要用 Collections.reverse(paths) 轉為 [2, 25, 225]
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 級聯更新所有關聯的資料
     * @CacheEvict: 失效模式，選單一但被修改後，即刪除 cache 中的資料
     * 1. 同時進行多種 cache 操作   @Caching
     * 2. 指定刪除某個分區下的所有資料
     * 3. 存儲同一類型的資料，都可以指定成同一個分區，分區名默認就是 cache 的前綴＊
     *
     * @Caching(evict = {　@CacheEvict(value = "category", key = "'getLevel1Categorys'"),
     * @CacheEvict(value = "category", key = "'getCatelogJson'")
     *
     */
    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //225 -> 25 -> 2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1. 收集當前節點 id
        paths.add(catelogId);
        //先找出當前節點
        CategoryEntity category = this.getById(catelogId);
        //如果該節點的父節點 id 不等於於 0，找其父節點
        if (category.getParentCid() != 0) {
            findParentPath(category.getParentCid(), paths);
        }
        return paths;
    }

    /**
     *
     * @param root 當前選單
     * @param all 當前選單的子選單
     * @return
     */
    //遞歸查找所有選單的子選單
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all){

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1. 找到子選單
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2)->{
            //2. 選單的排序
            return (menu1.getSort() == null ? 0:menu1.getSort()) - (menu2.getSort() == null ? 0:menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}