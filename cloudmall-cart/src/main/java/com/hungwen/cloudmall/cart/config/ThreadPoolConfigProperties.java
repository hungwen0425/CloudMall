package com.hungwen.cloudmall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-05 20:28
 **/

@ConfigurationProperties(prefix = "cloudmall.thread")
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
