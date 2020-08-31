package com.hungwen.cloudmall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CloudmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudmallMemberApplication.class, args);
    }

}
