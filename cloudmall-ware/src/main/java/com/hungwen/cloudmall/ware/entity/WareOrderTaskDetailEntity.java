package com.hungwen.cloudmall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 訂單工作單明細表
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetailEntity implements Serializable {

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
	 * 商品名稱
	 */
	private String skuName;
	/**
	 * 商品數量
	 */
	private Integer skuNum;
	/**
	 * 工作單 id
	 */
	private Long taskId;
	/**
	 * 倉庫 id
	 */
	private Long wareId;
	/**
	 * 鎖定狀態：1-->鎖定，2-->解鎖，3-->扣減
	 */
	private Integer lockStatus;
}
