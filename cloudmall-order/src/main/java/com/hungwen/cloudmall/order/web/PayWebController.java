package com.hungwen.cloudmall.order.web;

import com.hungwen.cloudmall.order.config.LinepayTemplate;
import com.hungwen.cloudmall.order.entity.OrderEntity;
import com.hungwen.cloudmall.order.service.OrderService;
import com.hungwen.cloudmall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-10 10:54
 **/
@Slf4j
@Controller
public class PayWebController {

    @Autowired
    LinepayTemplate linepayTemplate;
    @Autowired
    private OrderService orderService;

    /**
     * 用戶下單:LINE Pay 支付
     * 1、讓支付頁讓瀏覽器展示
     * 2、支付成功以後，跳轉到用戶的訂單列表頁
     * @param orderSn
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/aliPayOrder", produces = "text/html")
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws IOException {
        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = linepayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }

    // 根據訂單號查詢訂單狀態的API
    @GetMapping(value = "/queryByOrderId")
    @ResponseBody
    public OrderEntity queryByOrderId(@RequestParam("orderId") String orderId) {
        log.info("查詢支付記錄...");
        return orderService.getOrderByOrderSn(orderId);
    }
}
