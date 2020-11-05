package com.hungwen.cloudmall.auth.vo;

import lombok.Data;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-27 19:07
 **/

@Data
public class UserLoginVo {
    private String loginacct;
    private String password;
}
