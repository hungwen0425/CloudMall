package com.hungwen.cloudmall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-29 18:49
 **/

/**
 * 定時任務
 *      1. @EnableScheduling 開啟定時任務
 *      2. @Scheduled開啟一個定時任務
 *
 * 異步任務
 *      1. @EnableAsync:開啟異步任務
 *      2. @Async：給希望異步執行的方法標註
 */

@Slf4j
@Component
// @EnableAsync
// @EnableScheduling
public class HelloScheduled {

    /**
     * 1. 在 Spring 中表達式是 6 位組成，不允許第 7 位的年份
     * 2. 在周幾的的位置，1-7 代表周一到周日
     * 3. 定時任務不該阻塞。默認是阻塞的
     *      3.1  可以讓業務以異步的方式，自己提交到線程池
     *              CompletableFuture.runAsync(() -> {
     *                  },execute);
     *
     *      3.2  支持定時任務線程池；設置 TaskSchedulingProperties
     *              spring.task.scheduling.pool.size: 5
     *
     *      3.3  讓定時任務異步執行異步任務
     *
     *      解決：使用異步任務 + 定時任務來完成定時任務不阻塞的功能
     *
     */
    // @Async
    // @Scheduled(cron = "*/5 * * ? * 4")
    // public void hello() {
    //     log.info("hello...");
    //     try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
    //
    // }

}
