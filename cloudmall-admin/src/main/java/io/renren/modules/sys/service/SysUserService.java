/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;


/**
 * 系统使用者
 *
 * @author hungwen.tseng@gmail.com
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 查詢使用者的所有權限
	 * @param userId  使用者ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查詢使用者的所有選單ID
	 */
	List<Long> queryAllMenuId(Long userId);

	/**
	 * 根據使用者名，查詢系统使用者
	 */
	SysUserEntity queryByUserName(String username);

	/**
	 * 保存使用者
	 */
	void saveUser(SysUserEntity user);
	
	/**
	 * 修改使用者
	 */
	void update(SysUserEntity user);
	
	/**
	 * 删除使用者
	 */
	void deleteBatch(Long[] userIds);

	/**
	 * 修改密碼
	 * @param userId       使用者ID
	 * @param password     原密碼
	 * @param newPassword  新密碼
	 */
	boolean updatePassword(Long userId, String password, String newPassword);
}
