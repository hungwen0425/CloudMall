/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 選單管理
 *
 * @author hungwen.tseng@gmail.com
 */
@Data
@TableName("sys_menu")
public class SysMenuEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 選單ID
	 */
	@TableId
	private Long menuId;

	/**
	 * 父選單ID，一級選單為0
	 */
	private Long parentId;
	
	/**
	 * 父選單名稱
	 */
	@TableField(exist=false)
	private String parentName;

	/**
	 * 選單名稱
	 */
	private String name;

	/**
	 * 選單URL
	 */
	private String url;

	/**
	 * 授權(多個用逗號分隔，如：user:list,user:create)
	 */
	private String perms;

	/**
	 * 類型     0：目錄   1：選單   2：按钮
	 */
	private Integer type;

	/**
	 * 選單圖標
	 */
	private String icon;

	/**
	 * 排序
	 */
	private Integer orderNum;
	
	/**
	 * ztree属性
	 */
	@TableField(exist=false)
	private Boolean open;

	@TableField(exist=false)
	private List<?> list;

}
