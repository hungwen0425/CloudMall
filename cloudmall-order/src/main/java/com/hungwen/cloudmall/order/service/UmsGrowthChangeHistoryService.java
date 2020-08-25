package com.hungwen.cloudmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.order.entity.UmsGrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成長值變化歷史紀錄
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:57:51
 */
public interface UmsGrowthChangeHistoryService extends IService<UmsGrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

