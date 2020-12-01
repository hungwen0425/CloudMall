/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 系统日誌
 *
 * @author hungwen.tseng@gmail.com
 */
@Data
@TableName("sys_log")
public class SysLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId
	private Long id;
	//使用者名
	private String username;
	//使用者操作
	private String operation;
	//請求方法
	private String method;
	//請求参數
	private String params;
	//執行時長(毫秒)
	private Long time;
	//IP地址
	private String ip;
	//創建時間
	private Date createDate;

}
