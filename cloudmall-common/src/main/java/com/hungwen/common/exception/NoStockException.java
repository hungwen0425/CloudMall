package com.hungwen.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description TODO
 * @Author Hungwen Tseng
 * @Date 2020/6/20 07:28
 * @Version 1.0
 **/
public class NoStockException extends RuntimeException {

    @Getter @Setter
    private Long skuId;

    public NoStockException(Long skuId) {
        super("商品 id："+ skuId + "庫存不足！");
    }

    public NoStockException(String msg) {
        super(msg);
    }


}
