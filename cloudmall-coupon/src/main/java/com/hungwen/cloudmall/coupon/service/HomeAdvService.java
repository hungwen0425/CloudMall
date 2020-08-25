package com.hungwen.cloudmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首頁輪播廣告
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:40:00
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

