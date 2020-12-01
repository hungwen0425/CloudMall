/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.sys.entity.SysRoleMenuEntity;

import java.util.List;



/**
 * 角色與選單對應關係
 *
 * @author hungwen.tseng@gmail.com
 */
public interface SysRoleMenuService extends IService<SysRoleMenuEntity> {
	
	void saveOrUpdate(Long roleId, List<Long> menuIdList);
	
	/**
	 * 根據角色ID，取得選單ID列表
	 */
	List<Long> queryMenuIdList(Long roleId);

	/**
	 * 根據角色ID陣列，批量删除
	 */
	int deleteBatch(Long[] roleIds);
	
}
