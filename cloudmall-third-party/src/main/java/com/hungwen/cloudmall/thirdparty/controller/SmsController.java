package com.hungwen.cloudmall.thirdparty.controller;

import com.hungwen.cloudmall.thirdparty.component.SmsComponent;
import com.hungwen.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description TODO
 * @Author Hungwen Tseng
 * @Date 2020/6/27 09:50
 * @Version 1.0
 **/
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    SmsComponent smsComponent;
    /**
     * 提供給别的服務調用
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsComponent.sendCode(phone, code);
        return R.ok();
    }
}
