package com.hungwen.cloudmall.member.interceptor;

import com.hungwen.common.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

import static com.hungwen.common.constant.ums.AuthServerConstant.LOGIN_USER;

/**
 * @Description: 登入攔截器
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-25 18:37
 **/

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if (match) {
            return true;
        }

        HttpSession session = request.getSession();
        //查詢登入的用戶資料
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(LOGIN_USER);
        if (attribute != null) {
            //把登入後用戶的資料放在ThreadLocal裡面進行保存
            loginUser.set(attribute);
            return true;
        } else {
            //未登入，返回登入頁面
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('請先進行登入，再進行後續操作！');location.href='http://auth.cloudmall.com/login.html'</script>");
            // session.setAttribute("msg", "請先進行登入");
            // response.sendRedirect("http://auth.cloudmall.com/login.html");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
