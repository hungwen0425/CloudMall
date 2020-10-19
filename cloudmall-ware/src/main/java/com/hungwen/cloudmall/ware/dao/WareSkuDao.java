package com.hungwen.cloudmall.ware.dao;

import com.hungwen.cloudmall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品庫存主檔
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("spuId")Long skuId, @Param("wareId")Long wareId, @Param("skuNum")Integer skuNum);

    Long getSkuStock(@Param("item") Long item);
}
