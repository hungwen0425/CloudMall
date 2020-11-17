package com.hungwen.cloudmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu資料
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@Data
@TableName("pms_spu_info")
public class SpuInfoEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId
	private Long id;
	/**
	 * 商品名稱
	 */
	private String spuName;
	/**
	 * 商品描述
	 */
	private String spuDescription;
	/**
	 * 所屬分類id
	 */
	private Long catalogId;
	/**
	 * 品牌id
	 */
	private Long brandId;
	/**
	 * 品牌名
	 */
	@TableField(exist = false)
	private String brandName;
	/**
	 * 比重
	 */
	private BigDecimal weight;
	/**
	 * 上架狀態[0 - 下架，1 - 上架]
	 */
	private Integer publishStatus;
	/**
	 * 創建時間
	 */
	private Date createTime;
	/**
	 * 更新時間
	 */
	private Date updateTime;

}
