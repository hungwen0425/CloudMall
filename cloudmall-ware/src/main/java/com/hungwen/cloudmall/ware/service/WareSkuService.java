package com.hungwen.cloudmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.cloudmall.ware.vo.SkuHasStockVo;
import com.hungwen.cloudmall.ware.vo.WareSkuLockVo;
import com.hungwen.common.to.mq.OrderTo;
import com.hungwen.common.to.mq.StockLockedTo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.ware.entity.WareSkuEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 商品庫存主檔
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 15:02:59
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 添加庫存
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);
    /**
     * 判斷是否有庫存
     * @param skuIds
     * @return
     */
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);
    /**
     * 鎖定庫存
     * @param vo
     * @return
     */
    boolean orderLockStock(WareSkuLockVo vo);
    /**
     * 解鎖庫存
     * @param to
     */
    void unlockStock(StockLockedTo to);
    /**
     * 解鎖訂單
     * @param orderTo
     */
    void unlockStock(OrderTo orderTo);
}

