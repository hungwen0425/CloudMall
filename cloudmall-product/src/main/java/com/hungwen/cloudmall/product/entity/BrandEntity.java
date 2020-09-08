package com.hungwen.cloudmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.hungwen.common.valid.AddGroup;
import com.hungwen.common.valid.ListValue;
import com.hungwen.common.valid.UpdateGroup;
import com.hungwen.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改必需要指定品牌id", groups ={UpdateGroup.class} )
	@Null(message = "新增不能指定id",groups = {AddGroup.class})
	@TableId
	private Long brandId;

	/**
	 * 品牌名稱
	 */
	@NotBlank(message = "品牌名稱必需提交", groups = {UpdateGroup.class,AddGroup.class})
	private String name;

	/**
	 * 品牌 logo 地址
	 */
	@NotEmpty(groups = {AddGroup.class})
	@URL(message = "log 必需是一個合法的 URL 地址", groups ={UpdateGroup.class,AddGroup.class})
	private String logo;

	/**
	 * 介绍
	 */
	private String descript;

	/**
	 * 顯示狀態 [0-不顯示；1-顯示]
	 */
	@NotNull(groups ={AddGroup.class, UpdateStatusGroup.class})
	@ListValue(vals = {0,1}, groups ={AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;

	/**
	 * 檢索首字母
	 */
	@NotEmpty(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "該字段必需是一個 a-z 或 A-Z 的字母", groups ={UpdateGroup.class,AddGroup.class} )
	private String firstLetter;

	/**
	 * 排序
	 */
	@NotNull(groups = {AddGroup.class})
	@Min(value = 0,message = "排序必需要大於或等於零", groups ={UpdateGroup.class,AddGroup.class})
	private Integer sort;

}