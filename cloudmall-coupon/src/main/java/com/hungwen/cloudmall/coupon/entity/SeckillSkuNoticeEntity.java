package com.hungwen.cloudmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 秒殺商品通知訂閱
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:33:48
 */
@Data
@TableName("sms_seckill_sku_notice")
public class SeckillSkuNoticeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * member_id
	 */
	private Long memberId;
	/**
	 * sku_id
	 */
	private Long skuId;
	/**
	 * 活動場次id
	 */
	private Long sessionId;
	/**
	 * 訂閱時間
	 */
	private Date subcribeTime;
	/**
	 * 發送時間
	 */
	private Date sendTime;
	/**
	 * 通知方式[0-短信，1-郵件]
	 */
	private Integer noticeType;

}
