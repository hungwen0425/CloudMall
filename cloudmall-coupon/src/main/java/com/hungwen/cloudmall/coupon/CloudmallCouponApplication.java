package com.hungwen.cloudmall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CloudmallCouponApplication {

    public static void main(String[] args) {

        SpringApplication.run(CloudmallCouponApplication.class, args);
    }

}
