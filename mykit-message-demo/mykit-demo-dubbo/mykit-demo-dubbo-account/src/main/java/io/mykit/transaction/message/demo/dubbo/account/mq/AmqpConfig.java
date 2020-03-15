/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.transaction.message.demo.dubbo.account.mq;

import io.mykit.transaction.message.core.service.MykitMqReceiveService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author binghe
 * @version 1.0.0
 * @description 配置
 */
@Configuration
public class AmqpConfig {

    private static final String EXCHANGE = "account";
    private static final String ROUTING_KEY = "account";


    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * <p>
     * <p>
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        //队列持久
        return new Queue("account", true);

    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(defaultExchange()).with(AmqpConfig.ROUTING_KEY);
    }

    @Autowired
    private MykitMqReceiveService mykitMqReceiveService;


    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    @ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        //设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            byte[] messageBody = message.getBody();
            //确认消息成功消费
            final Boolean success = mykitMqReceiveService.processMessage(messageBody);
            if (success) {
                try {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return container;
    }
}
