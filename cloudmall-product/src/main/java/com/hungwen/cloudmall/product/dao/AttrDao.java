package com.hungwen.cloudmall.product.dao;

import com.hungwen.cloudmall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品屬性
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
