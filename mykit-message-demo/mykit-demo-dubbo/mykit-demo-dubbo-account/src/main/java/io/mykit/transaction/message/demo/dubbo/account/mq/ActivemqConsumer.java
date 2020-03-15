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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author binghe
 * @version 1.0.0
 * @description ActiveMQ消费者
 */
@Component
@ConditionalOnProperty(prefix = "spring.activemq", name = "broker-url")
public class ActivemqConsumer {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivemqConsumer.class);


    private final MykitMqReceiveService mykitMqReceiveService;

    @Autowired
    public ActivemqConsumer(MykitMqReceiveService mykitMqReceiveService) {
        this.mykitMqReceiveService = mykitMqReceiveService;
    }


    @JmsListener(destination = "account",containerFactory = "queueListenerContainerFactory")
    public void receiveQueue(byte[] message) {
        LOGGER.info("=========扣减账户信息接收到Myth框架传入的信息==========");
        final Boolean success = mykitMqReceiveService.processMessage(message);
        if(success){
            //消费成功，消息出队列，否则不消费
        }
    }
}
