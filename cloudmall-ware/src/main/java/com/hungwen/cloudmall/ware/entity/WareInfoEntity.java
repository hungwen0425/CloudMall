package com.hungwen.cloudmall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 倉庫主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
@Data
@TableName("wms_ware_info")
public class WareInfoEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 倉庫 id
	 */
	@TableId
	private Long id;
	/**
	 * 倉庫名稱
	 */
	private String name;
	/**
	 * 倉庫地址
	 */
	private String address;
	/**
	 * 郵遞區號
	 */
	private String areacode;

}
