/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 使用者
 *
 * @author hungwen.tseng@gmail.com
 */
@Data
@TableName("tb_user")
public class UserEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 使用者ID
	 */
	@TableId
	private Long userId;
	/**
	 * 使用者名
	 */
	private String username;
	/**
	 * 手機號
	 */
	private String mobile;
	/**
	 * 密碼
	 */
	private String password;
	/**
	 * 創建時間
	 */
	private Date createTime;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
