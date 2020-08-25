package com.hungwen.cloudmall.order.dao;

import com.hungwen.cloudmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 訂單主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:57:51
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
