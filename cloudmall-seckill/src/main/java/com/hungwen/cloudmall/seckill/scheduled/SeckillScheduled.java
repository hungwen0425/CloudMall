package com.hungwen.cloudmall.seckill.scheduled;

import com.hungwen.cloudmall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-29 19:22
 **/
/**
 * 限時搶購商品定時上架
 *  每天晚上3點，上架最近三天需要三天限時搶購的商品
 *  當天00:00:00 - 23:59:59
 *  明天00:00:00 - 23:59:59
 *  後天00:00:00 - 23:59:59
 */
@Slf4j
@Service
public class SeckillScheduled {

    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedissonClient redissonClient;
    // 限時搶購商品上架功能的鎖
    private final String upload_lock = "seckill:upload:lock";
    // TODO 保證冪等性問題
    // cron 每分鐘、每小時、每天、每周、每月、每年定時執行
    // @Scheduled(cron = "*/5 * * * * ? ") // 每 5 秒執行一次
    @Scheduled(cron = "0 0 1/1 * * ? ")
    public void uploadSeckillSkuLatest3Days() {
        // 1.重復上架無需處理
        log.info("上架限時搶購的商品...");
        // 分佈式鎖，鎖的業務執行完成，狀態已經完成更新，釋放鎖以後，其他人獲取到就會拿到最新的狀
        RLock lock = redissonClient.getLock(upload_lock);
        try {
            // 加鎖
            lock.lock(10, TimeUnit.SECONDS);
            seckillService.uploadSeckillSkuLatest3Days();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
