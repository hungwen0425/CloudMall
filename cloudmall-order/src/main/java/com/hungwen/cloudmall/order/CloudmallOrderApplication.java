package com.hungwen.cloudmall.order;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用 RabbitMQ
 * 1、引入 amqp 場景 RabbitAutoConfiguration 自動生效
 * 2、自動設定了 RabbitConnectionFactoryBean、RabbitTemplate、AmqpAdmin、RabbitMessagingTemplate
 *    所有的屬性都是 spring.rabbit
 *    @ConfigurationProperties(prefix = "spring.rabbit")
 * 3. 給設定檔案設定 spring.rabbit 相關設定
 * 4. @EnableRabbit 開啟功能
 * 5. 監聽 message
 *    @RabbitListener: 類 + 方法上 (監聽那些對列即可)
 *    @RabbitHeader: 標在方法上 (重載區分不同的 message)
 *
 * 本地 Transaction 失效問題
 * 同一個物件內 Transaction 互調默認失效，原因：繞過了代理物件，Transaction 是使用代理物件來控制的
 * 如果發現 Transaction 加不上。開啟基於註解的 Transaction 功能  @EnableTransactionManagement，如果要真的開啟什麼功能就顯式的加上這個註解。。。。
 * Transaction 的最終解決方案；
 * 1. 普通加 Transaction。導入 jdbc-starter，@EnableTransactionManagement，加 @Transactional
 * 2. 方法自己調自己類裡面的加不上 Transaction。
 *   2.1 導入 aop 包，開啟代理物件的相關功能
 *       <dependency>
 *           <groupId>org.springframework.boot</groupId>
 *           <artifactId>spring-boot-starter-aop</artifactId>
 *       </dependency>
 *   2.2 取得當前類真正的代理物件，去掉方法即可
 *      2.2.1 @EnableAspectJAutoProxy(exposeProxy = true):暴露代理物件 開啟 aspectj 動態代理，對外暴露代理物件
 *   2.3 本類調用物件
 *       OrderServicceImpl orderService = (OrderServicceImpl) AopContext.currentProxy();
 *       orderService.b();
 *       orderService.c();
 * Seata 控制分佈式 transaction
 * 1. 每一個微服務先必須創建 undo_log
 * 2. 安裝 Transaction 協調器：seata-server https://github.com/seata/seata/releases
 * 3. 整合
 *   3.1 導入依賴 spring-cloud-starter-alibaba-seata seata-all-0.7.1
 *   3.2 解壓縮並啟動 seata-server
 *       registry.conf：註冊中心相關的設定，修改 registry type = "nacos"
 *   3.3 所有享用到分佈式 Transaction 的微服務，使用 seata DataSourcceProxy 代理自己的資料來源
 *   3.4 每個微服務都必須導入
 *       file.conf
 *       registry.conf   vgroupMapping.{microservice-name}-fescar-service-group = "microservice-name"
 *   3.5 啟動測試分佈式 transaction
 *   3.6 給分佈式大 transaction 的入口註釋 @GlobalTransactional
 *   3.7 每一個遠程的小 transaction 用 @Transactional
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableRedisHttpSession
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)
public class CloudmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudmallOrderApplication.class, args);
    }

}
