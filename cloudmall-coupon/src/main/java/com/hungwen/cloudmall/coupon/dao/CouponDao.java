package com.hungwen.cloudmall.coupon.dao;

import com.hungwen.cloudmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 優惠券資訊
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:33:48
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
