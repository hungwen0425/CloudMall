package com.hungwen.cloudmall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-09 20:07
 **/

@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;

}


