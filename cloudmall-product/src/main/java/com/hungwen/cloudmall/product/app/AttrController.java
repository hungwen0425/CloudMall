package com.hungwen.cloudmall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hungwen.cloudmall.product.entity.ProductAttrValueEntity;
import com.hungwen.cloudmall.product.service.ProductAttrValueService;
import com.hungwen.cloudmall.product.vo.AttrRespVo;
import com.hungwen.cloudmall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hungwen.cloudmall.product.service.AttrService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.R;



/**
 * 商品屬性
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    /*
     * 根據 spuId 資料查詢出對應的規格參數資料
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R listForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entityList = productAttrValueService.baseAttrListForSpu(spuId);
        return  R.ok().put("data", entityList);
    };

    /*
     * 查詢分類規格參數
     * 查詢分類銷售屬性
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String type) {
        //進行分頁查詢
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 資料  取得 AttrRespVo 資料
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 新增屬性【規格參數，銷售屬性】
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attrVo) {
        attrService.saveAttr(attrVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attrVo) {
        attrService.updateAttr(attrVo);
        return R.ok();
    }

    /*
     * 修改商品規格
     * @param spuId
     * @param entities
     * @return
     */
    @PostMapping("/update/{spuId}")
    public R update(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId,entities);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));
        return R.ok();
    }
}
