package com.hungwen.cloudmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu資料介紹
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

