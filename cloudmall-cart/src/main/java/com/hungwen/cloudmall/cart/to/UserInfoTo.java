package com.hungwen.cloudmall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-06 17:35
 **/
@ToString
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    /**
     * 是否為臨時用戶
     */
    private Boolean tempUser = false;

}
