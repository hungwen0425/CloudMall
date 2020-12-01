/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.app.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.LoginForm;

/**
 * 使用者
 *
 * @author hungwen.tseng@gmail.com
 */
public interface UserService extends IService<UserEntity> {

	UserEntity queryByMobile(String mobile);

	/**
	 * 使用者登入
	 * @param form    登入表單
	 * @return        返回使用者ID
	 */
	long login(LoginForm form);
}
