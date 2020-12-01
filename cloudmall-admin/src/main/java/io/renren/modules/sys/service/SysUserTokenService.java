/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.R;
import io.renren.modules.sys.entity.SysUserTokenEntity;

/**
 * 使用者Token
 *
 * @author hungwen.tseng@gmail.com
 */
public interface SysUserTokenService extends IService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param userId  使用者ID
	 */
	R createToken(long userId);

	/**
	 * 退出，修改token值
	 * @param userId  使用者ID
	 */
	void logout(long userId);

}
