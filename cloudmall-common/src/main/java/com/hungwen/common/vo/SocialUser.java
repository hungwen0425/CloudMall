package com.hungwen.common.vo;

import lombok.Data;

/**
 * @Description: 社交使用者資料
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-28 11:04
 **/
@Data
public class SocialUser {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
    private String socialUid;
    private String accessToken;
    private Long expiresIn;

}
