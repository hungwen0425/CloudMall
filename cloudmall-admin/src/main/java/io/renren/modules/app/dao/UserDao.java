/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.app.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 使用者
 *
 * @author hungwen.tseng@gmail.com
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

}
