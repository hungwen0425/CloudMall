package com.hungwen.cloudmall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.hungwen.cloudmall.auth.feign.MemberFeignService;
import com.hungwen.cloudmall.auth.feign.ThirdPartFeignService;
import com.hungwen.cloudmall.auth.vo.UserLoginVo;
import com.hungwen.cloudmall.auth.vo.UserRegisterVo;
import com.hungwen.common.constant.ums.AuthServerConstant;
import com.hungwen.common.exception.BizCodeEnum;
import com.hungwen.common.utils.R;
import com.hungwen.common.vo.MemberResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-24 10:37
 **/

@Controller
public class LoginController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        // 1. 介面防刷
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            // 活動存入 Redis 的時間，用當前時間減去存入 Redis 的時間，判斷使用者手機號碼是否在 60s 內發送驗證碼
            long currentTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - currentTime < 60000) {
                // 60s 內不能再發
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
            }
        }
        // 2. 驗證碼的再次效驗 Redis 存 key-phone, value-code
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        String redisStorage = code + "_" + System.currentTimeMillis();
        // 存入 Redis，防止同一個手機號碼在 60 秒內再次發送驗證碼
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone,
                redisStorage,5, TimeUnit.MINUTES);
        thirdPartFeignService.sendCode(phone, code);

        return R.ok();
    }

    /**
     * RedirectAttributes：重定向也可以保留資料，不會丟失
     * 使用者註冊
     * @return
     */
    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVo vos, BindingResult result, RedirectAttributes attributes) {
        // 如果有錯誤回到註冊頁面
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors
                    .toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors", errors);
            // 效驗出錯回到註冊頁面
            return "redirect:http://auth.cloudmall.com/reg.html";
        }
        // 1. 效驗驗證碼
        String code = vos.getCode();
        // 查詢存入 Redis 裡的驗證碼
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
        if (!StringUtils.isEmpty(redisCode)) {
            // 截取字串
            if (code.equals(redisCode.split("_")[0])) {
                // 刪除驗證碼：令牌機制
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
                // 驗證碼通過，真正註冊，調用遠程服務進行註冊
                R register = memberFeignService.register(vos);
                if (register.getCode() == 0) {
                    // 成功
                    return "redirect:http://auth.cloudmall.com/login.html";
                } else {
                    // 失敗
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", register.getData("msg", new TypeReference<String>(){}));
                    attributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.cloudmall.com/reg.html";
                }
            } else {
                // 效驗出錯回到註冊頁面
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "驗證碼錯誤");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.cloudmall.com/reg.html";
            }
        } else {
            // 效驗出錯回到註冊頁面
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "驗證碼錯誤");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.cloudmall.com/reg.html";
        }
    }

    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session) {
        //從 session 先取出來使用者的信息，判斷使用者是否已經登入過了
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        //如果使用者沒登入那就跳轉到登入頁面
        if (attribute == null) {
            return "login";
        } else {
            return "redirect:http://cloudmall.com";
        }
    }

    @PostMapping(value = "/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session) {
        //遠程登入
        R login = memberFeignService.login(vo);
        if (login.getCode() == 0) {
            Map<String, LinkedHashMap> data = (Map<String, LinkedHashMap>) login.get("data");
            //MemberResponseVo data = login.getData("data", new LinkedHashMap<MemberResponseVo>() {});
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:http://cloudmall.com";
        } else {
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg", new TypeReference<String>(){}));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.cloudmall.com/login.html";
        }
    }

    @GetMapping(value = "/loguot.html")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(AuthServerConstant.LOGIN_USER);
        request.getSession().invalidate();
        return "redirect:http://cloudmall.com";
    }
}
