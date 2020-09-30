package com.hungwen.cloudmall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品庫存主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
@Data
@TableName("wms_ware_sku")
public class WareSkuEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 商品 id
	 */
	private Long skuId;
	/**
	 * 倉庫 id
	 */
	private Long wareId;
	/**
	 * 庫存數量
	 */
	private Integer stock;
	/**
	 * 商品名稱
	 */
	private String skuName;
	/**
	 * 庫存鎖定
	 */
	private Integer stockLocked;

}
