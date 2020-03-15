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
package io.mykit.transaction.message.demo.motan.inventory.mq;

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
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivemqConsumer.class);

    private final MykitMqReceiveService mykitMqReceiveService;

    /**
     * Instantiates a new Activemq consumer.
     *
     * @param mykitMqReceiveService the myth mq receive service
     */
    @Autowired
    public ActivemqConsumer(MykitMqReceiveService mykitMqReceiveService) {
        this.mykitMqReceiveService = mykitMqReceiveService;
    }


    /**
     * Receive queue.
     *
     * @param message the message
     */
    @JmsListener(destination = "inventory", containerFactory = "queueListenerContainerFactory")
    public void receiveQueue(byte[] message) {
        LOGGER.info("=========motan扣减库存接收到Mykit框架传入的信息==========");
        mykitMqReceiveService.processMessage(message);

    }
}
