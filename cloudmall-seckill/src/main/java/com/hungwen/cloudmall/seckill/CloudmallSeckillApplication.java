package com.hungwen.cloudmall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1. 整合Sentinel
 *   1.1  導入依賴 spring-cloud-starter-alibaba-sentinel
 *   1.2  下載sentinel控制台
 *   1.3  配置 sentinel 控制臺地址信息
 *   1.4  在控制台調整參數、【默認所有的流控規則保存在內存中，重啟失效】
 *
 * 2. 每一個微服務都導入 actuator ：並配合 management.endpoints.web.exposure.include=*
 *
 * 3. 自定義 sentinel 流控返回的數據
 *
 * 4. 使用Sentinel來保護feign遠程調用，熔斷；
 *   4.1  調用方的熔斷保護：feign.sentinel.enable=true
 *   4.2  調用方手動指定遠程服務的降級策略。遠程服務被降級處理。觸發我們的熔斷回調方法
 *   4.3  超大瀏覽的時候，必須犧牲一些遠程服務。在服務的提供方（遠程服務）指定降級策略；
 *        提供方是在運行，但是不允許自己的業務邏輯，返回的是默認的降級數據（限流的數據）
 *
 * 5. 自定義受保護的資源
 *   5.1  代碼
 *          try (Entry entry = SphU.entry("seckillSkus")) {
 *              //業務邏輯
 *          } catch(Exception e) {}
 *   5.2  基於註解
 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CloudmallSeckillApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudmallSeckillApplication.class, args);
	}

}
