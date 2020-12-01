/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.sys.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 使用者與角色對應關係
 *
 * @author hungwen.tseng@gmail.com
 */
@Mapper
public interface SysUserRoleDao extends BaseMapper<SysUserRoleEntity> {
	
	/**
	 * 根據使用者ID，取得角色ID列表
	 */
	List<Long> queryRoleIdList(Long userId);


	/**
	 * 根據角色ID陣列，批量删除
	 */
	int deleteBatch(Long[] roleIds);
}
