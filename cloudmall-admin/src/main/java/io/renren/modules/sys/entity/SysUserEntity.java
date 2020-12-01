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
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 系统使用者
 *
 * @author hungwen.tseng@gmail.com
 */
@Data
@TableName("sys_user")
public class SysUserEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 使用者 ID
	 */
	@TableId
	private Long userId;
	/**
	 * 使用者名
	 */
	@NotBlank(message="使用者名不能為空", groups = {AddGroup.class, UpdateGroup.class})
	private String username;
	/**
	 * 密碼
	 */
	@NotBlank(message="密碼不能為空", groups = AddGroup.class)
	private String password;
	/**
	 * 盐
	 */
	private String salt;
	/**
	 * 電子郵件
	 */
	@NotBlank(message="電子郵件不能為空", groups = {AddGroup.class, UpdateGroup.class})
	@Email(message="電子郵件格式不正確", groups = {AddGroup.class, UpdateGroup.class})
	private String email;
	/**
	 * 手機號
	 */
	private String mobile;
	/**
	 * 狀態  0：禁用   1：正常
	 */
	private Integer status;
	/**
	 * 角色ID列表
	 */
	@TableField(exist=false)
	private List<Long> roleIdList;
	/**
	 * 創建者 Id
	 */
	private Long createUserId;
	/**
	 * 創建時間
	 */
	private Date createTime;
}
