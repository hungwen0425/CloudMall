package com.hungwen.cloudmall.order.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-08 17:41
 **/

@Configuration
public class MyRabbitConfig {

    private RabbitTemplate rabbitTemplate;

    /**
     * 訂製 RabbitTemplate
     * @param connectionFactory
     * @return
     */
    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConverter());
        initRabbitTemplate();
        return rabbitTemplate;
    }

    /**
     * 使用 Json 序列化機制，進行消息轉換
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制 RabbitTemplate
     * 1. 服務收到消息就會回調
     *      1. spring.rabbitmq.publisher-confirms: true
     *      2. 設定確認回調
     * 2. 消息正確抵達隊列就會進行回調
     *      1. spring.rabbitmq.publisher-returns: true
     *         spring.rabbitmq.template.mandatory: true
     *      2. 設定確認回調 ReturnCallback
     * 3、消費端確認 ( 保證每個消息都被正確消費，此時才可以 broker 刪除這個消息 )
     *      1. 默認是自動確認的，只要消息接收到，客戶端會自動確認，服務端就會移除這個消息
     *         問題：我們收到很多消息，自動回覆給服務器 ack，只有一個消息處理成功
     *              客戶端當機了，發生資料丟失
     *         解決：消費者手動處理模式，只要我們沒有明確告知 MQ 貨物被簽收 (沒有 ack)，消息就一直 Unchecked 狀態
     *              即使是客戶端當機，消息不會丟失，會重新變回 Ready，下一次有新的客戶連接進來就會發給它
     */
    // @PostConstruct  //MyRabbitConfig物件創建完成以後，執行這個方法
    public void initRabbitTemplate() {
        /**
         * 設定確認回調
         * 1、只要消息抵達 Broker 就 ack=true
         * correlationData：當前消息的唯一關聯資料(這個是消息的唯一id)
         * ack：消息是否成功收到
         * cause：失敗的原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("confirm...correlationData[" + correlationData + "]==>ack:[" + ack + "]==>cause:[" + cause + "]");
        });
        /**
         * 只要消息沒有投遞給指定的隊列，就觸發這個失敗回調
         * message：投遞失敗的消息詳細資料
         * replyCode：回覆的狀態碼
         * replyText：回覆的文本內容
         * exchange：當時這個消息發給哪個交換機
         * routingKey：當時這個消息用哪個路郵鍵
         */
        rabbitTemplate.setReturnCallback((message,replyCode,replyText,exchange,routingKey) -> {
            System.out.println("Fail Message["+message+"]==>replyCode["+replyCode+"]" +
                    "==>replyText["+replyText+"]==>exchange["+exchange+"]==>routingKey["+routingKey+"]");
        });
    }
}
