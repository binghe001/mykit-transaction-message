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

import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.core.service.MykitMqReceiveService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author binghe
 * @version 1.0.0
 * @description Kafka消费者
 */
@Component
@ConditionalOnProperty(prefix = "spring.kafka.consumer", name = "bootstrap-servers")
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private final MykitMqReceiveService mykitMqReceiveService;

    @Autowired
    public KafkaConsumer(MykitMqReceiveService mykitMqReceiveService) {
        this.mykitMqReceiveService = mykitMqReceiveService;
    }

    @KafkaListener(topics = {"inventory"})
    public void kafkaListener(ConsumerRecord<?, byte[]> record) {
        Optional<?> messages = Optional.ofNullable(record.value());
        if (messages.isPresent()) {
            byte[] msg = (byte[]) messages.get();
            LogUtil.debug(LOGGER, "接收到Mykit分布式框架消息对象：{}", () -> msg);
            mykitMqReceiveService.processMessage(msg);

        }
    }
}
