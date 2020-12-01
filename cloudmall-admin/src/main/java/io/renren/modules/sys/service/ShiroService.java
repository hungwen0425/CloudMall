/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.service;

import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.entity.SysUserTokenEntity;

import java.util.Set;

/**
 * shiro相關接口
 *
 * @author hungwen.tseng@gmail.com
 */
public interface ShiroService {
    /**
     * 取得使用者權限列表
     */
    Set<String> getUserPermissions(long userId);

    SysUserTokenEntity queryByToken(String token);

    /**
     * 根據使用者ID，查詢使用者
     * @param userId
     */
    SysUserEntity queryUser(Long userId);
}
