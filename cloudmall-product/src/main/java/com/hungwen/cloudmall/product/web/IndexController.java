package com.hungwen.cloudmall.product.web;

import com.hungwen.cloudmall.product.entity.CategoryEntity;
import com.hungwen.cloudmall.product.service.CategoryService;
import com.hungwen.cloudmall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-08 14:14
 **/

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/", "index.html"})
    private String indexPage(Model model) {
        // 查出所有的商品選單一級分類
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categories", categoryEntities);
        return "index";
    }

    //index/json/catalog.json
    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    @ResponseBody
    @GetMapping(value = "/hello")
    public String hello() {
        // 1. 獲取一把鎖，只要鎖的名字一樣，就是同一把鎖
        RLock myLock = redissonClient.getLock("my-lock");
        // 2. 加鎖
        //阻塞式等待。默認加的鎖都是 30s
        myLock.lock();
        // 鎖的自動續期，如果業務超長，運行期間自動鎖上新的 30s。不用擔心業務時間長，鎖自動過期被刪掉
        // 加鎖的業務只要運行完成，就不會給當前鎖續期，即使不手動解鎖，鎖默認會在30s內自動過期，不會產生死鎖問題

        // myLock.lock(10,TimeUnit.SECONDS);   // 10s 自動解鎖,自動解鎖時間一定要大於業務執行時間
        //問題：在鎖時間到了以後，不會自動續期
        // 如果我們傳遞了鎖的超時時間，就發送給 redis 執行腳本，進行占鎖 ，默認超時就是 我們制定的時間
        // 如果我們指定鎖的超時時間，就使用 lockWatchdogTimeout = 30 * 1000 【看門狗默認時間】
        // 只要占鎖成功，就會啟動一個定時任務【重新給鎖設定過期時間，新的過期時間就是看門狗的默認時間】,每隔 10s 都會自動的再次續期，
        // 續成 30s
        // internalLockLeaseTime 【看門狗時間】 / 3， 10s
        try {
            System.out.println("加鎖成功，執行業務..." + Thread.currentThread().getId());
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //3、解鎖：假設解鎖代碼沒有運行，Redisson 會不會出現死鎖
            System.out.println("釋放鎖..." + Thread.currentThread().getId());
            myLock.unlock();
        }
        return "hello";
    }

    /**
     * 保證一定能讀到最新資料，修改期間，寫鎖是一個排它鎖（互斥鎖、獨享鎖），讀鎖是一個共享鎖
     * 寫鎖沒釋放讀鎖必須等待
     * 讀 + 讀 ：相當於無鎖，並發讀，只會在Redis中記錄好，所有當前的讀鎖。他們都會同時加鎖成功
     * 寫 + 讀 ：必須等待寫鎖釋放
     * 寫 + 寫 ：阻塞方式
     * 讀 + 寫 ：有讀鎖。寫也需要等待
     * 只要有讀或者寫的存都必須等待
     * @return
     */
    @GetMapping(value = "/write")
    @ResponseBody
    public String writeValue() {
        String s = "";
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        try {
            //1、改資料加寫鎖，讀資料加讀鎖
            rLock.lock();
            s = UUID.randomUUID().toString();
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            ops.set("writeValue",s);
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    @GetMapping(value = "/read")
    @ResponseBody
    public String readValue() {
        String s = "";
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        //加讀鎖
        RLock rLock = readWriteLock.readLock();
        try {
            rLock.lock();
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            s = ops.get("writeValue");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 車庫停車
     * 3車位
     * 信號量也可以做分布式限流
     */
    @GetMapping(value = "/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire();     //查詢一個信號、查詢一個值,占一個車位
        boolean flag = park.tryAcquire();
        if (flag) {
            //執行業務
        } else {
            return "error";
        }
        return "ok=>" + flag;
    }

    @GetMapping(value = "/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();     //釋放一個車位
        return "ok";
    }

    /**
     * 放假、鎖門
     * 1班沒人了
     * 5個班，全部走完，我們才可以鎖大門
     * 分布式閉鎖
     */
    @GetMapping(value = "/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();       //等待閉鎖完成
        return "放假了...";
    }

    @GetMapping(value = "/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();       //計數-1
        return id + "班的人都走了...";
    }
}