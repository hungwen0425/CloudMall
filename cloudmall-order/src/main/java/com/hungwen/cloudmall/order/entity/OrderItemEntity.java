package com.hungwen.cloudmall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 訂單中所包含的商品
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:57:51
 */
@Data
@TableName("oms_order_item")
public class OrderItemEntity implements Serializable {
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
	private String orderSn;
	/**
	 * $column.comments
	 */
	private Long productId;
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
	private String productSn;
	/**
	 * $column.comments
	 */
	private BigDecimal productPrice;
	/**
	 * $column.comments
	 */
	private Integer productQuantity;
	/**
	 * $column.comments
	 */
	private Long productSkuId;
	/**
	 * $column.comments
	 */
	private String productSkuCode;
	/**
	 * $column.comments
	 */
	private Long productCategoryId;
	/**
	 * $column.comments
	 */
	private String sp1;
	/**
	 * $column.comments
	 */
	private String sp2;
	/**
	 * $column.comments
	 */
	private String sp3;
	/**
	 * $column.comments
	 */
	private String promotionName;
	/**
	 * $column.comments
	 */
	private BigDecimal promotionAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal couponAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal integrationAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal realAmount;
	/**
	 * $column.comments
	 */
	private Integer giftIntegration;
	/**
	 * $column.comments
	 */
	private Integer giftGrowth;
	/**
	 * $column.comments
	 */
	private String productAttr;

}
