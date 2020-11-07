package com.hungwen.common.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-28 14:51
 **/

@ToString
@Data
public class MemberResponseVo implements Serializable {

    private static final long serialVersionUID = 5573669251256409786L;

    private Long id;
    /**
     * 會員等級id
     */
    private Long levelId;
    /**
     * 用戶名
     */
    private String username;
    /**
     * 密碼
     */
    private String password;
    /**
     * 昵稱
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
     * 用戶來源
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
     * 社交登錄UID
     */
    private String socialUid;

    /**
     * 社交登錄TOKEN
     */
    private String accessToken;

    /**
     * 社交登錄過期時間
     */
    private long expiresIn;

}