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
package io.mykit.transaction.message.demo.dubbo.inventory.mq;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.exception.MQClientException;
import io.mykit.transaction.message.core.service.MykitMqReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

/**
 * @author binghe
 * @version 1.0.0
 * @description Aliyunmq消费者
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.aliyunmq", name = "broker-url")
@SuppressWarnings("all")
public class AliyunmqConsumer {

    private static final String TAG = "inventory";

    private final Environment env;

    private final MykitMqReceiveService mykitMqReceiveService;

    @Autowired
    public AliyunmqConsumer(Environment env, MykitMqReceiveService mykitMqReceiveService) {
        this.env = env;
        this.mykitMqReceiveService = mykitMqReceiveService;
    }

    @Bean
    public Consumer pushConsumer() throws MQClientException {
        /**
         * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ConsumerGroupName需要由应用来保证唯一
         */
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ConsumerId, env.getProperty("spring.aliyunmq.consumerId"));
        properties.setProperty(PropertyKeyConst.AccessKey, env.getProperty("spring.aliyunmq.accessKey"));
        properties.setProperty(PropertyKeyConst.SecretKey, env.getProperty("spring.aliyunmq.secretKey"));
        properties.setProperty(PropertyKeyConst.ONSAddr, env.getProperty("spring.aliyunmq.broker-url"));

        Consumer consumer = ONSFactory.createConsumer(properties);

        String topic = env.getProperty("spring.aliyunmq.topic");
        consumer.subscribe(topic, TAG, (message, consumeContext) -> {
            try {
                final byte[] body = message.getBody();
                mykitMqReceiveService.processMessage(body);
                return Action.CommitMessage;
            } catch (Exception e) {
                //消费失败
                return Action.ReconsumeLater;
            }
        });

        /**
         * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         */
        consumer.start();

        return consumer;
    }
}
