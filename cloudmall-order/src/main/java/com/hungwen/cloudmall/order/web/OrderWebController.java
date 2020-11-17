package com.hungwen.cloudmall.order.web;

import com.hungwen.cloudmall.order.service.OrderService;
import com.hungwen.cloudmall.order.vo.OrderConfirmVo;
import com.hungwen.cloudmall.order.vo.OrderSubmitVo;
import com.hungwen.cloudmall.order.vo.SubmitOrderResponseVo;
import com.hungwen.common.exception.NoStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: HungwenTseng
 * @createTime: 2020-11-09 18:35
 **/

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * 去結算確認頁
     * @param model
     * @param request
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping(value = "/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

    /**
     * 下單功能
     * @param vo
     * @return
     */
    @PostMapping(value = "/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes attributes) {
        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
            // 下單成功來到支付選擇頁
            // 下單失敗回到訂單確認頁重新確定訂單資料
            if (responseVo.getCode() == 0) {
                // 成功
                model.addAttribute("submitOrderResp", responseVo);
                return "pay";
            } else {
                String msg = "下單失敗：";
                switch (responseVo.getCode()) {
                    case 1: msg += "令牌訂單資料過期，請刷新再次提交";
                    break;
                    case 2: msg += "訂單商品價格發生變化，請確認後再次提交";
                    break;
                    case 3: msg += "庫存鎖定失敗，商品庫存不足";
                    break;
                }
                attributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.cloudmall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String message = ((NoStockException)e).getMessage();
                attributes.addFlashAttribute("msg", message);
            }
            return "redirect:http://order.cloudmall.com/toTrade";
        }
    }
}
