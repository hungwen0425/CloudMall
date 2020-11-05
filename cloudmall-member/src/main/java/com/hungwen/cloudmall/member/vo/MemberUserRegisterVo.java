package com.hungwen.cloudmall.member.vo;

import lombok.Data;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-27 15:37
 **/

@Data
public class MemberUserRegisterVo {
    private String userName;
    private String password;
    private String phone;
}
