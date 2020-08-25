package com.hungwen.cloudmall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 訂單退貨申請
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:57:51
 */
@Data
@TableName("oms_order_return_apply")
public class OrderReturnApplyEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId
	private Long id;
	/**
	 * $column.comments
	 */
	private Long orderId;
	/**
	 * $column.comments
	 */
	private Long companyAddressId;
	/**
	 * $column.comments
	 */
	private Long productId;
	/**
	 * $column.comments
	 */
	private String orderSn;
	/**
	 * $column.comments
	 */
	private Date createTime;
	/**
	 * $column.comments
	 */
	private String memberUsername;
	/**
	 * $column.comments
	 */
	private BigDecimal returnAmount;
	/**
	 * $column.comments
	 */
	private String returnName;
	/**
	 * $column.comments
	 */
	private String returnPhone;
	/**
	 * $column.comments
	 */
	private Integer status;
	/**
	 * $column.comments
	 */
	private Date handleTime;
	/**
	 * $column.comments
	 */
	private String productPic;
	/**
	 * $column.comments
	 */
	private String productName;
	/**
	 * $column.comments
	 */
	private String productBrand;
	/**
	 * $column.comments
	 */
	private String productAttr;
	/**
	 * $column.comments
	 */
	private Integer productCount;
	/**
	 * $column.comments
	 */
	private BigDecimal productPrice;
	/**
	 * $column.comments
	 */
	private BigDecimal productRealPrice;
	/**
	 * $column.comments
	 */
	private String reason;
	/**
	 * $column.comments
	 */
	private String description;
	/**
	 * $column.comments
	 */
	private String proofPics;
	/**
	 * $column.comments
	 */
	private String handleNote;
	/**
	 * $column.comments
	 */
	private String handleMan;
	/**
	 * $column.comments
	 */
	private String receiveMan;
	/**
	 * $column.comments
	 */
	private Date receiveTime;
	/**
	 * $column.comments
	 */
	private String receiveNote;

}
