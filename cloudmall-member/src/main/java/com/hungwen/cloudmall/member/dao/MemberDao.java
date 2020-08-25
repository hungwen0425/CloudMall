package com.hungwen.cloudmall.member.dao;

import com.hungwen.cloudmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 會員
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:49:31
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
