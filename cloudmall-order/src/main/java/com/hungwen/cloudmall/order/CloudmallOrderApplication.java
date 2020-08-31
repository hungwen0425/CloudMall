package com.hungwen.cloudmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CloudmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudmallOrderApplication.class, args);
    }

}
