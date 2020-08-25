package com.hungwen.cloudmall.order.dao;

import com.hungwen.cloudmall.order.entity.UmsGrowthChangeHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成長值變化歷史紀錄
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:57:51
 */
@Mapper
public interface UmsGrowthChangeHistoryDao extends BaseMapper<UmsGrowthChangeHistoryEntity> {
	
}
