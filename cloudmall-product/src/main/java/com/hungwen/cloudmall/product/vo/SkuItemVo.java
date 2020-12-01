package com.hungwen.cloudmall.product.vo;

import com.hungwen.cloudmall.product.entity.SkuImagesEntity;
import com.hungwen.cloudmall.product.entity.SkuInfoEntity;
import com.hungwen.cloudmall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-19 16:46
 **/
@ToString
@Data
public class SkuItemVo {
    // 1. sku基本資料的查詢  pms_sku_info
    private SkuInfoEntity info;
    private boolean hasStock = true;
    // 2. sku的圖片資料    pms_sku_images
    private List<SkuImagesEntity> images;
    // 3. 查詢spu的銷售屬性組合
    private List<SkuItemSaleAttrVo> saleAttrs;
    // 4. 查詢spu的介紹
    private SpuInfoDescEntity desc;
    // 5. 查詢spu的規格參數資料
    private List<SpuItemAttrGroupVo> attrGroups;
    // 6、限時搶購商品的優惠資料
    private SecKillSkuVo secKillSkuVo;

}
