package com.hungwen.cloudmall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;


/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-16 18:39
 **/
@Configuration
public class MyRabbitMQConfig {
    // 容器中的 Queue、Exchange、Binding 會自動創建 ( 在 RabbitMQ 沒有) 的情況下
    /**
     * 死信隊列 (Dead Queue)
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        /*
            Queue(String name,  隊列名字
            boolean durable,  是否持久化
            boolean exclusive,  是否排他
            boolean autoDelete, 是否自動刪除
            Map<String, Object> arguments) 屬性
         */
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000); // 消息過期時間 1 分鐘
        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }
    /**
     * 普通隊列 (Normal Queue)
     * @return
     */
    @Bean
    public Queue orderReleaseQueue() {
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }
    /**
     * TopicExchange
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        /*
         *   String name,
         *   boolean durable,
         *   boolean autoDelete,
         *   Map<String, Object> arguments
         * */
        return new TopicExchange("order-event-exchange", true, false);
    }
    /**
     * 創建訂單的 Binding
     * @return
     */
    @Bean
    public Binding orderCreateBinding() {
        /*
         * String destination, 目的地（隊列名或者交換機名字）
         * DestinationType destinationType, 目的地類型（Queue、Exhcange）
         * String exchange,
         * String routingKey,
         * Map<String, Object> arguments
         * */
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,"order-event-exchange",
                            "order.create.order",null);
    }

    @Bean
    public Binding orderReleaseBinding() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,"order-event-exchange","order.release.order",null);
    }
    /**
     * 訂單釋放直接和庫存釋放進行綁定
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,"order-event-exchange",
                           "order.release.other.#",null);
    }
    /**
     * 商品秒殺隊列
     * @return
     */
    @Bean
    public Queue orderSecKillOrrderQueue() {
        Queue queue = new Queue("order.seckill.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Binding orderSecKillOrrderQueueBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			Map<String, Object> arguments
        Binding binding = new Binding("order.seckill.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.seckill.order",null);
        return binding;
    }
}
