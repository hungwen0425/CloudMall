package com.hungwen.cloudmall.member.web;

import com.alibaba.fastjson.JSON;
import com.hungwen.cloudmall.member.feign.OrderFeignService;
import com.hungwen.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-25 13:39
 **/

@Controller
public class MemberWebController {

    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping(value = "/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                                  Model model, HttpServletRequest request) {

        // 查詢 LINE Pay 給我們轉來的所有請求資料
        // request，驗證簽名
        // 查出當前登入用戶的所有訂單列表 資料
        Map<String,Object> page = new HashMap<>();
        page.put("page", pageNum.toString());
        // 遠程查詢訂單服務訂單資料
        R orderInfo = orderFeignService.listWithItem(page);
        System.out.println(JSON.toJSONString(orderInfo));
        model.addAttribute("orders", orderInfo);
        return "orderList";
    }

}
