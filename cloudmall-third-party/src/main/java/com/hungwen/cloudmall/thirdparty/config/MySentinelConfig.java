package com.hungwen.cloudmall.thirdparty.config;

//import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
//import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.context.annotation.Configuration;

/**
 * @Description TODO
 * @Author Hungwen TSeng
 * @Date 2020/6/13 09:03
 * @Version 1.0
 **/
@Configuration
public class MySentinelConfig {

//    public MySentinelConfig() {
//        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
//            @Override
//            public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
//                R error = R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMsg());
//                httpServletResponse.setCharacterEncoding("UTF-8");
//                httpServletResponse.setContentType("application/json");
//                httpServletResponse.getWriter().write(JSON.toJSONString(error));
//            }
//        });
//    }
}
