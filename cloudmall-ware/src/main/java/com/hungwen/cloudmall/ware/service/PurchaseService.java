package com.hungwen.cloudmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.cloudmall.ware.vo.MergeVo;
import com.hungwen.cloudmall.ware.vo.PurchaseFinishVo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 採購需求主檔
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void received(List<Long> ids);

    PageUtils queryPageUnreceived(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void done(PurchaseFinishVo finishVo);
}

