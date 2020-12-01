package com.hungwen.cloudmall.seckill.service;


import com.hungwen.cloudmall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-29 19:29
 **/
public interface SeckillService {

    /**
     * 上架三天需要限時搶購的商品
     */
    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 根據 skuId 查詢商品是否參加限時搶購活動
     * @param skuId
     * @return
     */
    SeckillSkuRedisTo getSkuSeckilInfo(Long skuId);

    /**
     * 當前商品進行限時搶購（限時搶購開始）
     * @param killId
     * @param key
     * @param num
     * @return
     */
    String kill(String killId, String key, Integer num) throws InterruptedException;
}
