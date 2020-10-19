package com.hungwen.cloudmall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-11 09:39
 **/

@Configuration
public class MyRedissonConfig {

    /**
     * 所有對 Redisson 的使用都是通過 RedissonClient
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        //1、創建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");
        //2、根據 Config 創建出 RedissonClient 實例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

}
