package com.hungwen.cloudmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hungwen.cloudmall.order.constant.OrderConstant;
import com.hungwen.cloudmall.order.constant.PayConstant;
import com.hungwen.cloudmall.order.entity.OrderItemEntity;
import com.hungwen.cloudmall.order.entity.PaymentInfoEntity;
import com.hungwen.cloudmall.order.enume.OrderStatusEnum;
import com.hungwen.cloudmall.order.feign.CartFeignService;
import com.hungwen.cloudmall.order.feign.MemberFeignService;
import com.hungwen.cloudmall.order.feign.ProductFeignService;
import com.hungwen.cloudmall.order.feign.WmsFeignService;
import com.hungwen.cloudmall.order.interceptor.LoginUserInterceptor;
import com.hungwen.cloudmall.order.service.OrderItemService;
import com.hungwen.cloudmall.order.service.PaymentInfoService;
import com.hungwen.cloudmall.order.to.OrderCreateTo;
import com.hungwen.cloudmall.order.to.SpuInfoTo;
import com.hungwen.cloudmall.order.vo.*;
import com.hungwen.common.constant.cart.CartConstant;
import com.hungwen.common.exception.NoStockException;
import com.hungwen.common.to.mq.OrderTo;
import com.hungwen.common.to.mq.SecKillOrderTo;
import com.hungwen.common.utils.R;
import com.hungwen.common.vo.MemberResponseVo;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.order.dao.OrderDao;
import com.hungwen.cloudmall.order.entity.OrderEntity;
import com.hungwen.cloudmall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private WmsFeignService wmsFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private BestPayService bestPayService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );
        return new PageUtils(page);
    }
    /**
     * 訂單確認頁返回需要用的資料
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResponseVo memberRespVo = LoginUserInterceptor.loginUser.get();
        // 解決Feign異步 ThreadLocal 問題
        // 查詢當前線程的資料
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            // 1. 遠程查詢所有收貨地址列表
            // 每一個線程都來共享當前請求的資料
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            // 2. 遠程查詢購物車所有選中的購物項
            // 每一個線程都來共享當前請求的資料
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentCartItems();
            confirmVo.setItems(items);
            // feign 在遠程調用之前要構造請求，調用很多的攔截器
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());

            R hasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuStockVo> data = hasStock.getData(new TypeReference<List<SkuStockVo>>() {});
            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, executor);

        // 3. 查詢用戶積分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);
        // 4. 其他資料自動計算
        // 5. 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        // 服務器放入一個 token
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        // 頁面也放入同一個 token
        confirmVo.setOrderToken(token);
        CompletableFuture.allOf(getAddressFuture, cartFuture).get();
        return confirmVo;
    }
    /**
     * 提交訂單
     * @param vo
     * @return
     */
    // @Transactional(isolation = Isolation.READ_COMMITTED) 設置事務的隔離級別
    // @Transactional(propagation = Propagation.REQUIRED)   設置事務的傳播級別
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        // 去創建、下訂單、驗令牌、驗價格、鎖定庫存...
        // 查詢當前用戶登入的資料
        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();
        responseVo.setCode(0);
        // 1. 驗證令牌是否合法【令牌的對比和刪除必須保證原子性】
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        // 通過 lure 腳本原子驗證令牌和刪除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId()),
                orderToken);
        if (result == 0L) {
            // 令牌驗證失敗
            responseVo.setCode(1);
            return responseVo;
        } else {
            // 令牌驗證成功
            // 1. 創建訂單、訂單項等資料
            OrderCreateTo order = createOrder();
            // 2. 驗證價格
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                // 金額對比
                // 3. 保存訂單
                saveOrder(order);
                // 4. 庫存鎖定，只要有異常，回滾訂單資料
                // 訂單號、所有訂單項資料(skuId, skuNum, skuName)
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                // 查詢出要鎖定的商品資料資料
                List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());

                lockVo.setLocks(orderItemVos);
                // 調用遠程鎖定庫存的方法
                // 出現的問題：扣減庫存成功了，但是由於網路原因超時，出現異常，導致訂單事務回滾，庫存事務不回滾 (解決方案：seata)
                // 為了保證高併發，不推薦使用 seata，因為是加鎖，並行化，提升不了效率，可以發消息給庫存服務
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    // 鎖定成功
                    responseVo.setOrder(order.getOrder());
                    // int i = 10/0;
                    // 訂單創建成功，發送消息給 MQ，判斷過期訂單
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order", order.getOrder());
                    // 刪除購物車裡的資料
                    redisTemplate.delete(CartConstant.CART_PREFIX + memberResponseVo.getId());
                    return responseVo;
                } else {
                    // 鎖定失敗
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }
            } else {
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }
    /**
     * 創建訂單
     * @param
     * @return
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo createTo = new OrderCreateTo();
        // 1. 生成訂單號
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = builderOrder(orderSn);
        // 2. 查詢到所有的訂單項
        List<OrderItemEntity> orderItemEntities = builderOrderItems(orderSn);
        // 3. 驗價 (計算價格、積分等 資料)
        computePrice(orderEntity, orderItemEntities);
        createTo.setOrder(orderEntity);
        createTo.setOrderItems(orderItemEntities);
        return createTo;
    }
    /**
     * 按照訂單號查詢訂單 資料
     * @param orderSn
     * @return
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return orderEntity;
    }
    /**
     * 查詢當前用戶所有訂單資料
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
                        .eq("member_id",memberResponseVo.getId()).orderByDesc("create_time")
        );
        // 遍歷所有訂單集合
        List<OrderEntity> orderEntityList = page.getRecords().stream().map(order -> {
            // 根據訂單號查詢訂單項裡的資料
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>()
                    .eq("order_sn", order.getOrderSn()));
            order.setOrderItemEntityList(orderItemEntities);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(orderEntityList);

        return new PageUtils(page);
    }
    /**
     * 查詢當前訂單的支付資料
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderInfo = this.getOrderByOrderSn(orderSn);
        // 保留兩位小數點，向上取值
        BigDecimal payAmount = orderInfo.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(payAmount.toString());
        payVo.setOut_trade_no(orderInfo.getOrderSn());
        // 查詢訂單項的資料
        List<OrderItemEntity> orderItemInfo = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItemEntity = orderItemInfo.get(0);
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        payVo.setSubject(orderItemEntity.getSkuName());
        return payVo;
    }
    /**
     * 定時關閉訂單
     **/
    @Override
    public void closeOrder(OrderEntity orderEntity) {
        // 關閉訂單之前先查詢一下資料庫，判斷此訂單狀態是否已支付
        OrderEntity orderInfo = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn",orderEntity.getOrderSn()));

        if (orderInfo.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            // 代付款狀態進行關單
            OrderEntity orderUpdate = new OrderEntity();
            orderUpdate.setId(orderInfo.getId());
            orderUpdate.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(orderUpdate);
            // 發送消息給 MQ
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderInfo, orderTo);
            try {
                // 確保每個消息發送成功，給每個消息做好日誌記錄，(給資料庫保存每一個詳細資料) 保存每個消息的詳細資料
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                //TODO 定期掃描資料庫，重新發送失敗的消息
            }
        }
    }

    /**
     * 處理支付寶的支付結果
     * @param asyncVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handlePayResult(PayAsyncVo asyncVo) {
        // 保存交易流水資料
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(asyncVo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(asyncVo.getTrade_no());
        paymentInfo.setTotalAmount(new BigDecimal(asyncVo.getBuyer_pay_amount()));
        paymentInfo.setSubject(asyncVo.getBody());
        paymentInfo.setPaymentStatus(asyncVo.getTrade_status());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(asyncVo.getNotify_time());
        // 添加到資料庫中
        paymentInfoService.save(paymentInfo);
        // 修改訂單狀態
        // 查詢當前狀態
        String tradeStatus = asyncVo.getTrade_status();
        if (tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")) {
            // 支付成功狀態
            // 查詢訂單號
            String orderSn = asyncVo.getOut_trade_no();
            updateOrderStatus(orderSn, OrderStatusEnum.PAYED.getCode(), PayConstant.ALIPAY);
        }
        return "success";
    }
    /**
     * 異步通知結果
     * @param notifyData
     * @return
     */
    @Override
    public String asyncNotify(String notifyData) {
        // 簽名效驗
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}", payResponse);
        // 2. 金額效驗（從資料庫查訂單）
        OrderEntity orderEntity = this.getOrderByOrderSn(payResponse.getOrderId());

        // 如果查詢出來的資料是 null 的話
        // 比較嚴重(正常情況下是不會發生的)發出告警：釘釘、簡訊
        if (orderEntity == null) {
            //TODO 發出告警，釘釘，簡訊
            throw new RuntimeException("通過訂單編號查詢出來的結果是null");
        }
        // 判斷訂單狀態狀態是否為已支付或者是已取消,如果不是訂單狀態不是已支付狀態
        Integer status = orderEntity.getStatus();
        if (status.equals(OrderStatusEnum.PAYED.getCode()) || status.equals(OrderStatusEnum.CANCLED.getCode())) {
            throw new RuntimeException("該訂單已失效, orderNo=" + payResponse.getOrderId());
        }
        // 3. 修改訂單支付狀態
        // 支付成功狀態
        String orderSn = orderEntity.getOrderSn();
        this.updateOrderStatus(orderSn,OrderStatusEnum.PAYED.getCode(),PayConstant.WXPAY);
        //4.告訴微信不要再重復通知了
        return "<xml>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
    }

    /**
     * 創建秒殺單
     * @param orderTo
     */
    @Override
    public void createSeckillOrder(SecKillOrderTo orderTo) {
        // 保存訂單資料
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
        orderEntity.setCreateTime(new Date());
        BigDecimal totalPrice = orderTo.getSeckillPrice().multiply(BigDecimal.valueOf(orderTo.getNum()));
        orderEntity.setPayAmount(totalPrice);
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        this.save(orderEntity);
        // 保存訂單項資料
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderSn(orderTo.getOrderSn());
        orderItem.setRealAmount(totalPrice);
        orderItem.setSkuQuantity(orderTo.getNum());
        // 保存商品的 spu 資料
        R spuInfo = productFeignService.getSpuInfoBySkuId(orderTo.getSkuId());
        SpuInfoTo spuInfoData = spuInfo.getData("data", new TypeReference<SpuInfoTo>(){});
        orderItem.setSpuId(spuInfoData.getId());
        orderItem.setSpuName(spuInfoData.getSpuName());
        orderItem.setSpuBrand(spuInfoData.getBrandName());
        orderItem.setCategoryId(spuInfoData.getCatalogId());
        // 保存訂單項資料
        orderItemService.save(orderItem);
    }

    /**
     * 修改訂單狀態
     * @param orderSn
     * @param code
     */
    private void updateOrderStatus(String orderSn, Integer code, Integer payType) {
        this.baseMapper.updateOrderStatus(orderSn, code, payType);
    }


    /**
     * 計算價格的方法
     * @param orderEntity
     * @param orderItemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        // 總價
        BigDecimal total = new BigDecimal("0.0");
        // 優惠價
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        // 積分、成長值
        Integer integrationTotal = 0;
        Integer growthTotal = 0;
        // 訂單總額，疊加每一個訂單項的總額 資料
        for (OrderItemEntity orderItem : orderItemEntities) {
            // 優惠價格 資料
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());
            // 總價
            total = total.add(orderItem.getRealAmount());
            // 積分 資料和成長值 資料
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();
        }
        // 1. 訂單價格相關的
        orderEntity.setTotalAmount(total);
        // 設置應付總額(總額+運費)
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);
        // 設置積分成長值 資料
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);
        // 設置刪除狀態(0-未刪除，1-已刪除)
        orderEntity.setDeleteStatus(0);
    }

    /**
     * 構建訂單資料
     * @param orderSn
     * @return
     */
    private OrderEntity builderOrder(String orderSn) {
        // 查詢當前用戶登入 資料
        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberUsername(memberResponseVo.getUsername());
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        // 遠程查詢收貨地址和運費 資料
        R fareAddressVo = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = fareAddressVo.getData("data", new TypeReference<FareVo>() {});
        // 查詢到運費 資料
        BigDecimal fare = fareResp.getFare();
        orderEntity.setFreightAmount(fare);
        // 查詢到收貨地址 資料
        MemberAddressVo address = fareResp.getAddress();
        // 設置收貨人 資料
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        // 設置訂單相關的狀態 資料
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);
        return orderEntity;
    }

    /**
     * 保存訂單所有資料
     * @param orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        // 查詢訂單資料
        OrderEntity order = orderCreateTo.getOrder();
        order.setModifyTime(new Date());
        order.setCreateTime(new Date());
        // 保存訂單
        this.baseMapper.insert(order);
        // 查詢訂單項資料
        List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems();
        // 批量保存訂單項資料
        orderItemService.saveBatch(orderItems);
    }

    /**
     * 構建所有訂單項資料
     * @return
     */
    public List<OrderItemEntity> builderOrderItems(String orderSn) {
        // 最後確認每個購物項的價格
        List<OrderItemVo> items = cartFeignService.getCurrentCartItems();
        if (items != null && items.size() > 0) {
            // 構建訂單項資料
            List<OrderItemEntity> itemEntities = items.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    /**
     * 構建某一個訂單項的資料
     * @param items
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo items) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // 1. 商品的 spu 資料
        Long skuId = items.getSkuId();
        // 查詢 spu 的 資料
        R spuInfo = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoTo spuInfoData = spuInfo.getData("data", new TypeReference<SpuInfoTo>() {
        });
        orderItemEntity.setSpuId(spuInfoData.getId());
        orderItemEntity.setSpuName(spuInfoData.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoData.getBrandName());
        orderItemEntity.setCategoryId(spuInfoData.getCatalogId());
        // 2. 商品的sku 資料
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(items.getTitle());
        orderItemEntity.setSkuPic(items.getImage());
        orderItemEntity.setSkuPrice(items.getPrice());
        orderItemEntity.setSkuQuantity(items.getCount());
        //使用 StringUtils.collectionToDelimitedString 將 list 集合轉換為 String
        String skuAttrValues = StringUtils.collectionToDelimitedString(items.getSkuAttrValues(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);
        // 3. 商品的優惠 資料

        // 4. 商品的積分 資料
        orderItemEntity.setGiftGrowth(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());
        orderItemEntity.setGiftIntegration(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());
        // 5. 訂單項的價格 資料
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
        // 當前訂單項的實際金額.總額 - 各種優惠價格
        // 原來的價格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        // 原價減去優惠價得到最終的價格
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);

        return orderItemEntity;
    }
}