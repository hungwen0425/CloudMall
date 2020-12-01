package com.hungwen.cloudmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hungwen.cloudmall.cart.exception.CartExceptionHandler;
import com.hungwen.cloudmall.cart.feign.ProductFeignService;
import com.hungwen.cloudmall.cart.interceptor.CartInterceptor;
import com.hungwen.cloudmall.cart.service.CartService;
import com.hungwen.cloudmall.cart.to.UserInfoTo;
import com.hungwen.cloudmall.cart.vo.CartItemVo;
import com.hungwen.cloudmall.cart.vo.CartVo;
import com.hungwen.cloudmall.cart.vo.SkuInfoVo;
import com.hungwen.common.constant.cart.CartConstant;
import com.hungwen.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-06-30 17:06
 **/

@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 1. 拿到要操作的購物車資料
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        // 判斷 Redis 是否有該商品的資料
        String productRedisValue = (String) cartOps.get(skuId.toString());
        // 如果購物車沒有商品就添加
        if (StringUtils.isEmpty(productRedisValue)) {
            // 2. 添加新的商品到購物車 (Redis)
            CartItemVo cartItemVo = new CartItemVo();
            // 開啟第一個異步任務
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                // 1、遠程查詢當前要添加商品的資料
                R productSkuInfo = productFeignService.getInfo(skuId);
                SkuInfoVo skuInfo = productSkuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                // 資料賦值操作
                cartItemVo.setSkuId(skuInfo.getSkuId());
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                cartItemVo.setPrice(skuInfo.getPrice());
                cartItemVo.setCount(num);
            }, executor);
            // 開啟第二個異步任務
            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                // 2、遠程查詢 skuAttrValues 組合資料
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttrValues(skuSaleAttrValues);
            }, executor);
            // 等待所有的異步任務全部完成
            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();
            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), cartItemJson);
            return cartItemVo;
        } else {
            // 購物車有此商品，修改數量即可
            CartItemVo cartItemVo = JSON.parseObject(productRedisValue, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            // 修改 Redis 的資料
            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), cartItemJson);
            return cartItemVo;
        }
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        //拿到要操作的購物車資料
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String redisValue = (String) cartOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(redisValue, CartItemVo.class);
        return cartItemVo;
    }

    /**
     * 查詢用戶登入或者未登入購物車裡所有的資料
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            // 1. 登入
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            // 臨時購物車的 key
            String temptCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            // 2. 如果臨時購物車的資料還未進行合併
            List<CartItemVo> tempCartItems = getCartItems(temptCartKey);
            if (tempCartItems != null) {
                // 臨時購物車有資料需要進行合併操作
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                // 清除臨時購物車的資料
                clearCartInfo(temptCartKey);
            }
            // 3. 查詢登入後的購物車資料【包含合併過來的臨時購物車的資料和登入後購物車的資料】
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        } else {
            // 沒登入
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            // 查詢臨時購物車裡面的所有購物項
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }
        return cartVo;
    }

    /**
     * 查詢到我們要操作的購物車
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        // 先得到當前用戶資料
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            // cloudmall:cart:1
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }
        // 綁定指定的 key 操作 Redis
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    /**
     * 查詢購物車裡面的資料
     * @param cartKey
     * @return
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        // 查詢購物車裡面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> cartItemVoStream = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVo cartItem = JSON.parseObject(str, CartItemVo.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;
    }

    /**
     * 清空購物車的資料
     * @param cartKey
     */
    @Override
    public void clearCartInfo(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    /**
     * 勾選購物項
     * @param skuId
     * @param check
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        // 查詢購物車裡面的商品
        CartItemVo cartItem = getCartItem(skuId);
        // 修改商品狀態
        cartItem.setCheck(check == 1 ? true : false);
        // 序列化存入 Redis 中
        String redisValue = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), redisValue);
    }

    /**
     * 修改購物項數量
     * @param skuId
     * @param num
     */
    @Override
    public void changeItemCount(Long skuId, Integer num) {
        // 查詢購物車裡面的商品
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        // 序列化存入 Redis 中
        String redisValue = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), redisValue);
    }

    /**
     * 刪除購物項
     * @param skuId
     */
    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    /**
     * 查詢當前用戶的購物車商品項
     * @return
     */
    @Override
    public List<CartItemVo> getUserCartItems() {
        List<CartItemVo> cartItemVoList = new ArrayList<>();
        // 查詢當前用戶登入的資料
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        // 如果用戶未登入直接返回 null
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            // 查詢購物車項
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            // 查詢所有的
            List<CartItemVo> cartItems = getCartItems(cartKey);
            if (cartItems == null) {
                throw new CartExceptionHandler();
            }
            // 篩選出勾選中的
            cartItemVoList = cartItems.stream()
                .filter(items -> items.getCheck())
                .map(item -> {
                    // 更新為最新的價格（查詢資料庫）
                    R price = productFeignService.getPrice(item.getSkuId());
                    String data = (String) price.get("data");
                    item.setPrice(new BigDecimal(data));
                    return item;
                }).collect(Collectors.toList());
        }
        return cartItemVoList;
    }
}
