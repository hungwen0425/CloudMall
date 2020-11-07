package com.hungwen.cloudmall.cart.service;


import com.hungwen.cloudmall.cart.vo.CartItemVo;
import com.hungwen.cloudmall.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-05 17:06
 **/
public interface CartService {

    /**
     * 將商品添加至購物車
     * @param skuId
     * @param num
     * @return
     */
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;
    /**
     * 查詢購物車某個購物項
     * @param skuId
     * @return
     */
    CartItemVo getCartItem(Long skuId);
    /**
     * 查詢購物車裡面的資訊
     * @return
     */
    CartVo getCart() throws ExecutionException, InterruptedException;
    /**
     * 清空購物車的資料
     * @param cartKey
     */
    public void clearCartInfo(String cartKey);
    /**
     * 勾選購物項
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);
    /**
     * 改變商品數量
     * @param skuId
     * @param num
     */
    void changeItemCount(Long skuId, Integer num);
    /**
     * 刪除購物項
     * @param skuId
     */
    void deleteIdCartInfo(Integer skuId);

    List<CartItemVo> getUserCartItems();

}
