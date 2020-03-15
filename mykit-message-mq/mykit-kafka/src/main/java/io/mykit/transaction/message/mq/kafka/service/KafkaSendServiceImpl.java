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
package io.mykit.transaction.message.mq.kafka.service;

import io.mykit.transaction.message.core.service.MykitMqSendService;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author binghe
 * @version 1.0.0
 * @description KafkaSendServiceImpl
 */
public class KafkaSendServiceImpl implements MykitMqSendService {

    private KafkaTemplate kafkaTemplate;

    /**
     * Sets kafka template.
     *
     * @param kafkaTemplate the kafka template
     */
    public void setKafkaTemplate(final KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendMessage(final String destination, final Integer pattern, final byte[] message) {
        kafkaTemplate.send(destination, message);
    }
}
