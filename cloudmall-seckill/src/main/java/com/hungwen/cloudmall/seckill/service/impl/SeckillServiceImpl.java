package com.hungwen.cloudmall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hungwen.cloudmall.seckill.feign.CouponFeignService;
import com.hungwen.cloudmall.seckill.feign.ProductFeignService;
import com.hungwen.cloudmall.seckill.interceptor.LoginUserInterceptor;
import com.hungwen.cloudmall.seckill.service.SeckillService;
import com.hungwen.cloudmall.seckill.to.SeckillSkuRedisTo;
import com.hungwen.cloudmall.seckill.vo.SeckillSessionWithSkusVo;
import com.hungwen.cloudmall.seckill.vo.SkuInfoVo;
import com.hungwen.common.to.mq.SecKillOrderTo;
import com.hungwen.common.utils.R;
import com.hungwen.common.vo.MemberResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-07-09 19:29
 **/

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    private final String SECKILL_CHARE_PREFIX = "seckill:skus";
    // 商品隨機碼
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 1. 掃描最近三天的商品需要參加限時搶購的活動
        R lates3DaySession = couponFeignService.getLates3DaySession();
        if (lates3DaySession.getCode() == 0) {
            // 上架商品
            List<SeckillSessionWithSkusVo> sessionData = lates3DaySession.getData("data",
                                                                new TypeReference<List<SeckillSessionWithSkusVo>>(){});
            // 緩存到 Redis
            // 1. 緩存活動資料
            saveSessionInfos(sessionData);
            // 2. 緩存活動的關聯商品資料
            saveSessionSkuInfo(sessionData);
        }
    }

    /**
     * 緩存限時搶購活動資料
     * @param sessions
     */
    private void saveSessionInfos(List<SeckillSessionWithSkusVo> sessions) {
        sessions.stream().forEach(session -> {
            // 查詢當前活動的開始和結束時間的時間戳
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            // 存入到 Redis 中的 key
            String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
            // 判斷 Redis 中是否有該資料，如果沒有才進行添加
            Boolean hasKey = redisTemplate.hasKey(key);
            // 緩存活動資料
            if (!hasKey) {
                // 查詢活動中所有商品的 skuId
                List<String> skuIds = session.getRelationSkus().stream()
                        .map(item -> item.getPromotionSessionId() + "-" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, skuIds);
            }
        });
    }

    /**
     * 緩存限時搶購活動所關聯的商品資料
     * @param sessions
     */
    private void saveSessionSkuInfo(List<SeckillSessionWithSkusVo> sessions) {
        sessions.stream().forEach(session -> {
            // 準備 hash 操作，綁定 hash
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                // 生成隨機碼
                String token = UUID.randomUUID().toString().replace("-", "");
                String redisKey = seckillSkuVo.getPromotionSessionId().toString() + "-" + seckillSkuVo.getSkuId().toString();
                if (!operations.hasKey(redisKey)) {
                    // 緩存我們商品資料
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    Long skuId = seckillSkuVo.getSkuId();
                    // 1. 先查詢 sku的 基本資料，調用遠程服務
                    R info = productFeignService.getSkuInfo(skuId);
                    if (info.getCode() == 0) {
                        SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>(){});
                        redisTo.setSkuInfo(skuInfo);
                    }
                    // 2. sku 的限時搶購資料
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);
                    // 3. 設置當前商品的限時搶購時間資料
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());
                    // 4. 設置商品的隨機碼（防止惡意攻擊）
                    redisTo.setRandomCode(token);
                    // 序列化 json 格式存入 Redis 中
                    String seckillValue = JSON.toJSONString(redisTo);
                    operations.put(seckillSkuVo.getPromotionSessionId().toString() + "-" + seckillSkuVo.getSkuId().toString(), seckillValue);
                    // 如果當前這個場次的商品庫存資料已經上架，就不需要上架
                    // 5. 使用庫存作為分佈式 Redisson 信號量（限流）
                    // 使用庫存作為分佈式信號量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    // 商品可以限時搶購的數量作為信號量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }
            });
        });
    }

    public List<SeckillSkuRedisTo> blockHandler(BlockException e) {
        log.error("getCurrentSeckillSkusResource 被限流了,{}", e.getMessage());
        return null;
    }

    /**
     * 查詢當前可以參加限時搶購商品的資料
     * @return
     */
    @Override
    @SentinelResource(value = "getCurrentSeckillSkusResource", blockHandler = "blockHandler")
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        try (Entry entry = SphU.entry("seckillSkus")) {
            // 1. 確定當前屬於哪個限時搶購場次
            long currentTime = System.currentTimeMillis();
            // 從 Redis 中查詢到所有 key 以 seckill:sessions 開頭的所有資料
            Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
            for (String key : keys) {
                // seckill:sessions:1594396764000_1594453242000
                String replace = key.replace(SESSION_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                // 查詢存入 Redis 商品的開始時間
                long startTime = Long.parseLong(s[0]);
                // 查詢存入 Redis 商品的結束時間
                long endTime = Long.parseLong(s[1]);
                // 判斷是否是當前限時搶購場次
                if (currentTime >= startTime && currentTime <= endTime) {
                    // 2. 查詢這個限時搶購場次需要的所有商品資料
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hasOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
                    assert range != null;
                    List<String> listValue = hasOps.multiGet(range);
                    if (listValue != null && listValue.size() >= 0) {
                        List<SeckillSkuRedisTo> collect = listValue.stream().map(item -> {
                            String items = (String) item;
                            SeckillSkuRedisTo redisTo = JSON.parseObject(items, SeckillSkuRedisTo.class);
                            // redisTo.setRandomCode(null); 當前限時搶購開始需要隨機碼
                            return redisTo;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                    break;
                }
            }
        } catch (BlockException e) {
            log.error("資源被限流{}", e.getMessage());
        }
        return null;
    }

    /**
     * 根據 skuId 查詢商品是否參加限時搶購活動
     * @param skuId
     * @return
     */
    @Override
    public SeckillSkuRedisTo getSkuSeckilInfo(Long skuId) {
        // 1. 找到所有需要限時搶購的商品的 key 資料---seckill:skus
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        // 拿到所有的 key
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            // 4-45 正則表達式進行匹配
            String regx = "\\d-" + skuId;
            for (String key : keys) {
                // 如果匹配上了
                if (Pattern.matches(regx, key)) {
                    // 從 Redis 中取出資料來
                    String redisValue = hashOps.get(key);
                    // 進行序列化
                    SeckillSkuRedisTo redisTo = JSON.parseObject(redisValue, SeckillSkuRedisTo.class);
                    // 隨機碼
                    Long currentTime = System.currentTimeMillis();
                    Long startTime = redisTo.getStartTime();
                    Long endTime = redisTo.getEndTime();
                    // 如果當前時間大於等於限時搶購活動開始時間並且要小於活動結束時間
                    if (currentTime >= startTime && currentTime <= endTime) {
                        return redisTo;
                    }
                    redisTo.setRandomCode(null);
                    return redisTo;
                }
            }
        }
        return null;
    }

    /**
     * 當前商品進行限時搶購（限時搶購開始）
     *
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        long s1 = System.currentTimeMillis();
        // 查詢當前用戶的資料
        MemberResponseVo user = LoginUserInterceptor.loginUser.get();
        // 1. 查詢當前限時搶購商品的詳細資料從 Redis 中獲取
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        String skuInfoValue = hashOps.get(killId);
        if (StringUtils.isEmpty(skuInfoValue)) {
            return null;
        }
        // 合法性效驗
        SeckillSkuRedisTo redisTo = JSON.parseObject(skuInfoValue, SeckillSkuRedisTo.class);
        Long startTime = redisTo.getStartTime();
        Long endTime = redisTo.getEndTime();
        long currentTime = System.currentTimeMillis();
        // 判斷當前這個限時搶購請求是否在活動時間區間內 (效驗時間的合法性)
        if (currentTime >= startTime && currentTime <= endTime) {
            // 2. 效驗隨機碼和商品 id
            String randomCode = redisTo.getRandomCode();
            String promotionSessionIdSkuId = redisTo.getPromotionSessionId() + "-" + redisTo.getSkuId();
            if (randomCode.equals(key) && killId.equals(promotionSessionIdSkuId)) {
                // 3. 驗證購物數量是否合理和庫存量是否充足
                Integer seckillLimit = redisTo.getSeckillLimit();
                // 獲取信號量
                String seckillCount = redisTemplate.opsForValue().get(SKU_STOCK_SEMAPHORE + randomCode);
                Integer count = Integer.valueOf(seckillCount);
                // 判斷信號量是否大於 0，並且買的數量不能超過庫存
                if (count > 0 && num <= seckillLimit && count > num) {
                    // 4. 驗證這個人是否已經買過了（冪等性處理），如果限時搶購成功，就去占位。userId-sessionId-skuId
                    // SETNX 原子性處理
                    String redisKey = user.getId() + "-" + promotionSessionIdSkuId;
                    // 設置自動過期 (活動結束時間 - 當前時間)
                    Long ttl = endTime - currentTime;
                    Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                    if (aBoolean) {
                        // 占位成功說明從來沒有買過，分佈式鎖 (獲取信號量 - 1)
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                        // 限時搶購成功，快速下單
                        boolean semaphoreCount = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                        // 保證 Redis 中還有商品庫存
                        if (semaphoreCount) {
                            // 創建訂單號和訂單資料發送給 MQ
                            // 限時搶購成功 快速下單 發送消息到 MQ 整個操作時間在 10ms 左右
                            String timeId = IdWorker.getTimeId();
                            SecKillOrderTo orderTo = new SecKillOrderTo();
                            orderTo.setOrderSn(timeId);
                            orderTo.setMemberId(user.getId());
                            orderTo.setNum(num);
                            orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                            orderTo.setSkuId(redisTo.getSkuId());
                            orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                            // 發送創建訂單的消息
                            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", orderTo);
                            long s2 = System.currentTimeMillis();
                            log.info("耗時..." + (s2 - s1));
                            return timeId;
                        }
                    }
                }
            }
        }
        long s3 = System.currentTimeMillis();
        log.info("耗時..." + (s3 - s1));
        return null;
    }
}
