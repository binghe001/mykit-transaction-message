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
package io.mykit.transaction.message.mq.jms.service;

import io.mykit.transaction.message.annotation.MessageTypeEnum;
import io.mykit.transaction.message.core.service.MykitMqSendService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import java.util.Objects;

/**
 * @author binghe
 * @version 1.0.0
 * @description ActivemqSendServiceImpl
 */
public class ActivemqSendServiceImpl implements MykitMqSendService {

    private JmsTemplate jmsTemplate;

    public void setJmsTemplate(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void sendMessage(final String destination, final Integer pattern, final byte[] message) {
        Destination queue = new ActiveMQQueue(destination);
        if (Objects.equals(MessageTypeEnum.P2P.getCode(), pattern)) {
            queue = new ActiveMQQueue(destination);
        } else if (Objects.equals(MessageTypeEnum.TOPIC.getCode(), pattern)) {
            queue = new ActiveMQTopic(destination);
        }
        jmsTemplate.convertAndSend(queue, message);
    }

}
