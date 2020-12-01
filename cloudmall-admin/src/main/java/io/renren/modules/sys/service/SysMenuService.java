/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.sys.entity.SysMenuEntity;

import java.util.List;


/**
 * 選單管理
 *
 * @author hungwen.tseng@gmail.com
 */
public interface SysMenuService extends IService<SysMenuEntity> {

	/**
	 * 根據父選單，查詢子選單
	 * @param parentId 父選單ID
	 * @param menuIdList  使用者選單ID
	 */
	List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList);

	/**
	 * 根據父選單，查詢子選單
	 * @param parentId 父選單ID
	 */
	List<SysMenuEntity> queryListParentId(Long parentId);
	
	/**
	 * 取得不包含按钮的選單列表
	 */
	List<SysMenuEntity> queryNotButtonList();
	
	/**
	 * 取得使用者選單列表
	 */
	List<SysMenuEntity> getUserMenuList(Long userId);

	/**
	 * 删除
	 */
	void delete(Long menuId);
}
