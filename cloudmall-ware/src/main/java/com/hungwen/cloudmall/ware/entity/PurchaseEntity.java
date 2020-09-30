package com.hungwen.cloudmall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 採購需求主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
@Data
@TableName("wms_purchase")
public class PurchaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 採購 id
	 */
	@TableId
	private Long id;
	/**
	 * 受託人 id
	 */
	private Long assigneeId;
	/**
	 * 受託人名稱
	 */
	private String assigneeName;
	/**
	 * 聯繫電話
	 */
	private String phone;
	/**
	 * 優先級別
	 */
	private Integer priority;
	/**
	 * 狀態
	 */
	private Integer status;
	/**
	 * 倉庫 id
	 */
	private Long wareId;
	/**
	 * 數量
	 */
	private BigDecimal amount;
	/**
	 * 創建時間
	 */
	private Date createTime;
	/**
	 * 更新時間
	 */
	private Date updateTime;

}
