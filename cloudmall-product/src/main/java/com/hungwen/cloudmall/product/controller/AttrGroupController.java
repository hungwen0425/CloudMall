package com.hungwen.cloudmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hungwen.cloudmall.product.entity.AttrEntity;
import com.hungwen.cloudmall.product.service.AttrAttrgroupRelationService;
import com.hungwen.cloudmall.product.service.AttrService;
import com.hungwen.cloudmall.product.service.CategoryService;
import com.hungwen.cloudmall.product.vo.AttrGroupRelationVo;
import com.hungwen.cloudmall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hungwen.cloudmall.product.entity.AttrGroupEntity;
import com.hungwen.cloudmall.product.service.AttrGroupService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.R;



/**
 * 屬性分組
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrService attrService;

//    @PostMapping("/attr/relation")
//    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){
//        attrAttrgroupRelationService.saveBatch(vos);
//        return R.ok();
//    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        //1、查出當前分類下的所有屬性分組，
        // 255 -> [1, 2, 4]
        //2、查出每個屬性分組的所有屬性
        // [1, 2, 4] -> [2, 3, 4, 5, 6, 7, 8, 9] -> List<AttrGroupWithAttrsVo>
        List<AttrGroupWithAttrsVo> vos =  attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }

    // /product/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
//    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
//        List<AttrEntity> entities =  attrService.getRelationAttr(attrgroupId);
//        return R.ok().put("data", entities);
//    }
//
//    // 頁面傳遞過來的分頁參數，@RequestParam Map<String, Object> params
//    @GetMapping("/{attrgroupId}/noattr/relation")
//    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
//                            @RequestParam Map<String, Object> params){
//        PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);
//        return R.ok().put("page", page);
//    }
//
//    @PostMapping("/attr/relation/delete")
//    public R deleteRelation(@RequestBody  AttrGroupRelationVo[] vos){
//        attrAttrgroupRelationService.deleteRelation(vos);
//        return R.ok();
//    }

    /**
     *
     * 查詢 AttrGroup 備註
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        return R.ok();
    }
}
