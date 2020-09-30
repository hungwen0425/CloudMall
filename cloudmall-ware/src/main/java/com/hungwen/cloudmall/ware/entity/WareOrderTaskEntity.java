package com.hungwen.cloudmall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 庫存工作單主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
@Data
@TableName("wms_ware_order_task")
public class WareOrderTaskEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 訂單 id
	 */
	private Long orderId;
	/**
	 * 訂單流水號
	 */
	private String orderSn;
	/**
	 * 收貨人
	 */
	private String consignee;
	/**
	 * 收貨人電話
	 */
	private String consigneeTel;
	/**
	 * 收貨地址
	 */
	private String deliveryAddress;
	/**
	 * 訂單備註
	 */
	private String orderComment;
	/**
	 * 付款方式
	 */
	private Integer paymentWay;
	/**
	 * 工作單狀態
	 */
	private Integer taskStatus;
	/**
	 * 訂單實體
	 */
	private String orderBody;
	/**
	 * 追蹤號碼
	 */
	private String trackingNo;
	/**
	 * 創建時間
	 */
	private Date createTime;
	/**
	 * 倉庫 id
	 */
	private Long wareId;
	/**
	 * 發貨備註
	 */
	private String taskComment;

}
