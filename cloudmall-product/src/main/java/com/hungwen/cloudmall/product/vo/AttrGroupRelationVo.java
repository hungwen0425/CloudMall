package com.hungwen.cloudmall.product.vo;

import lombok.Data;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSeng
 * @createTime: 2020-10-29 17:21
 **/

@Data
public class AttrGroupRelationVo {
    //[{"attrId":1,"attrGroupId":2}]
    private Long attrId;
    private Long attrGroupId;

}
