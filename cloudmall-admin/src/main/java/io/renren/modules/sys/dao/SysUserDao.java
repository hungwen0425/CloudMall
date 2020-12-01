/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统使用者
 *
 * @author hungwen.tseng@gmail.com
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUserEntity> {
	
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

}
