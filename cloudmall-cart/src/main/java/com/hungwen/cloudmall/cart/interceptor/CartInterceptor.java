package com.hungwen.cloudmall.cart.interceptor;

import com.hungwen.cloudmall.cart.to.UserInfoTo;
import com.hungwen.common.constant.cart.CartConstant;
import com.hungwen.common.constant.ums.AuthServerConstant;
import com.hungwen.common.vo.MemberResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;


/**
 * @Description: 在執行目標方法之前，判斷用戶的登入狀態，並封裝傳遞給 controller 目標請求
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-05 17:31
 **/
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /***
     * 目標方法執行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        // 查詢當前登入用戶的資料
        MemberResponseVo memberResponseVo = (MemberResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberResponseVo != null) {
            // 用戶登入了
            userInfoTo.setUserId(memberResponseVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                // user-key
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    userInfoTo.setUserKey(cookie.getValue());
                    // 標記為已是臨時用戶
                    userInfoTo.setTempUser(true);
                }
            }
        }
        // 如果沒有臨時用戶一定分配一個臨時用戶
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        // 目標方法執行之前
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 業務執行之後，分配臨時用戶來瀏覽器保存
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 查詢當前用戶的值
        UserInfoTo userInfoTo = threadLocal.get();
        // 如果沒有臨時用戶一定保存一個臨時用戶
        if (!userInfoTo.getTempUser()) {
            // 創建一個 cookie
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            // 擴大作用域
            cookie.setDomain("cloudmall.com");
            // 設置過期時間
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
