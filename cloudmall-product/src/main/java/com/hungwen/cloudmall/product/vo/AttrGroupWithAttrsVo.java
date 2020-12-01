package com.hungwen.cloudmall.product.vo;

import com.hungwen.cloudmall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSEng
 * @createTime: 2020-10-31 10:07
 **/

@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分組id
     */
    private Long attrGroupId;
    /**
     * 組名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 組圖標
     */
    private String icon;
    /**
     * 所屬分類id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
