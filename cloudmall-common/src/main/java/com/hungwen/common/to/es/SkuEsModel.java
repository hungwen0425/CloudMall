package com.hungwen.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuEsModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Integer saleCount;
    private Boolean hasStock;
    private long hotScore;
    private long brandId;
    private String brandName;
    private String brandImg;
    private Long catalogId;
    private String catalogName;
    private List<Attrs> attrs;

    @Data
    public static class Attrs {
        private long attrId;
        private String attrName;
        private String attrValue;
    }
}
