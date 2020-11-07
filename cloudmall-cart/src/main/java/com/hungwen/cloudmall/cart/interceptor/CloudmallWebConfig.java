package com.hungwen.cloudmall.cart.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSeng
 * @createTime: 2020-11-06 17:57
 **/
@Configuration
public class CloudmallWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 註冊攔截器
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
