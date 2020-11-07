package com.hungwen.cloudmall.cart.exception;

import com.hungwen.common.utils.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: 统一異常處理
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-05 21:09
 **/

@ControllerAdvice
public class RuntimeExceptionHandler {

    /**
     * 全局统一異常處理v
     * @param exception
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R handler(RuntimeException exception) {
        return R.error(exception.getMessage());
    }

    @ExceptionHandler(CartExceptionHandler.class)
    public R userHandler(CartExceptionHandler exception) {
        return R.error("購物車無此商品");
    }
}
