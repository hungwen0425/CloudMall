package com.hungwen.cloudmall.order.vo;

import lombok.Data;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-09 19:03
 **/

@Data
public class MemberAddressVo {
    /**
     * id
     */
    private Long id;
    /**
     * member_id
     */
    private Long memberId;
    /**
     * 收貨人姓名
     */
    private String name;
    /**
     * 電話
     */
    private String phone;
    /**
     * 郵政編碼
     */
    private String postCode;
    /**
     * 省份/直轄市
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 區
     */
    private String region;
    /**
     * 詳細地址(街道)
     */
    private String detailAddress;
    /**
     * 省市區代碼
     */
    private String areacode;
    /**
     * 是否默認
     */
    private Integer defaultStatus;
}
