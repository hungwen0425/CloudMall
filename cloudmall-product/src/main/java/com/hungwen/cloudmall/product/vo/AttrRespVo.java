package com.hungwen.cloudmall.product.vo;

import lombok.Data;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSeng
 * @createTime: 2020-10-29 09:26
 **/

@Data
public class AttrRespVo extends AttrVo {
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
}
