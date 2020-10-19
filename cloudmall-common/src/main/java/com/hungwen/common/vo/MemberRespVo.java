package com.hungwen.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description TODO
 * @Author Hungwen Tseng
 * @Date 2020/6/16 18:02
 * @Version 1.0
 **/
@ToString
@Data
public class MemberRespVo implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 會員等級 id
     */
    private Long levelId;
    /**
     * 使用者名稱
     */
    private String username;
    /**
     * 密碼
     */
    private String password;
    /**
     * 暱稱
     */
    private String nickname;
    /**
     * 手機號碼
     */
    private String mobile;
    /**
     * 郵箱
     */
    private String email;
    /**
     * 頭像
     */
    private String header;
    /**
     * 性別
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 職業
     */
    private String job;
    /**
     * 個性簽名
     */
    private String sign;
    /**
     * 使用者來源
     */
    private Integer sourceType;
    /**
     * 積分
     */
    private Integer integration;
    /**
     * 成長值
     */
    private Integer growth;
    /**
     * 啟用狀態
     */
    private Integer status;
    /**
     * 註冊時間
     */
    private Date createTime;
    /**
     * 會員社交登入識別標識
     */
    private String socialUid;
    /**
     * 訪問令牌
     */
    private String accessToken;
    /**
     * 訪問額令牌過期時間
     */
    private Long expiresIn;
}
