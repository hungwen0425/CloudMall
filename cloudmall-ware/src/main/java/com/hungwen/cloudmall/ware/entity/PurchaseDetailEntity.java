package com.hungwen.cloudmall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 採購需求明細主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
@Data
@TableName("wms_purchase_detail")
public class PurchaseDetailEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 採購 id
	 */
	private Long purchaseId;
	/**
	 * 商品 id
	 */
	private Long skuId;
	/**
	 * 商品數量
	 */
	private Integer skuNum;
	/**
	 * 商價價格
	 */
	private BigDecimal skuPrice;
	/**
	 * 倉庫 id
	 */
	private Long wareId;
	/**
	 * 狀態
	 */
	private Integer status;

}
