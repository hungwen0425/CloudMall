package com.hungwen.cloudmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.cloudmall.order.vo.*;
import com.hungwen.common.to.mq.SecKillOrderTo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 訂單主檔
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:57:51
 */

public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 訂單確認頁返回需要用的資料
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderByOrderSn(String orderSn);
    /**
     * 分頁查詢當前登入用戶的所有訂單 資料
     * @param params
     * @return
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    PayVo getOrderPay(String orderSn);
    /**
     * 定時關閉訂單
     * @param orderEntity
     * @return
     **/
    void closeOrder(OrderEntity orderEntity);
    /**
     * 修改訂單狀態
     * @param asyncVo
     * @return
     */
    String handlePayResult(PayAsyncVo asyncVo);
    /**
     * 異步通知結果
     * @param notifyData
     * @return
     */
    String asyncNotify(String notifyData);
    /**
     * 創建限時搶購單
     * @param orderTo
     */
    void createSeckillOrder(SecKillOrderTo orderTo);

}

