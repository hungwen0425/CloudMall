package com.hungwen.cloudmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hungwen.cloudmall.product.entity.CategoryEntity;
import com.hungwen.cloudmall.product.service.CategoryService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.R;



/**
 * 商品三級分類主檔
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查詢所有分類以及子分類，並以樹形結構組裝起來
     */
    @RequestMapping("/list/tree")
    public R list(){
        List<CategoryEntity> entities = categoryService.listWithTree();
        return R.ok().put("data", entities);
    }

    /**
     * 資料
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);
        return R.ok();
    }

    /**
     * 批量更新
     *
     * @param categorys
     * @return
     */
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] categorys) {
        categoryService.updateBatchById(Lists.newArrayList(categorys));
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
        categoryService.updateCascade(category);
        return R.ok();
    }

    /**
     * 删除
     * @RequestBody: 取得請求體，必須發送 POST 請求
     * SpringMVC 自動將請求體的資訊 (Json) ，轉為對應的物件
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
        //1. 檢查當前刪除的選單，是否被別的地方引用
        //categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
