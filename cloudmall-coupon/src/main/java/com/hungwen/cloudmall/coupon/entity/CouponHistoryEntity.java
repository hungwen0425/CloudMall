package com.hungwen.cloudmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 優惠券領取歷史記錄
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:33:48
 */
@Data
@TableName("sms_coupon_history")
public class CouponHistoryEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 優惠券id
	 */
	private Long couponId;
	/**
	 * 會員id
	 */
	private Long memberId;
	/**
	 * 會員名字
	 */
	private String memberNickName;
	/**
	 * 查詢方式[0->後台贈送；1->主動領取]
	 */
	private Integer getType;
	/**
	 * 創建時間
	 */
	private Date createTime;
	/**
	 * 使用狀態 [0->未使用；1->已使用；2->已過期]
	 */
	private Integer useType;
	/**
	 * 使用時間
	 */
	private Date useTime;
	/**
	 * 訂單id
	 */
	private Long orderId;
	/**
	 * 訂單號
	 */
	private Long orderSn;

}
