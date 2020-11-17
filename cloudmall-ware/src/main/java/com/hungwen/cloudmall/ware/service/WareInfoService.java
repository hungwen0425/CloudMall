package com.hungwen.cloudmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.cloudmall.ware.vo.FareVo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 倉庫主檔
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);

}

