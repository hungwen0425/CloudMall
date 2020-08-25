package com.hungwen.cloudmall.ware.dao;

import com.hungwen.cloudmall.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 倉庫主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
