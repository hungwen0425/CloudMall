package com.hungwen.cloudmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class CloudmallGatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(CloudmallGatewayApplication.class, args);
    }

}
