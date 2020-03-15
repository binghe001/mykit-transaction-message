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
package io.mykit.transaction.message.mq.rocketmq.service;

import com.google.common.base.Splitter;
import io.mykit.transaction.message.common.constant.CommonConstant;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.core.service.MykitMqSendService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author binghe
 * @version 1.0.0
 * @description RocketmqSendServiceImpl
 */
public class RocketmqSendServiceImpl implements MykitMqSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketmqSendServiceImpl.class);

    private DefaultMQProducer defaultMQProducer;

    /**
     * Sets default mq producer.
     *
     * @param defaultMQProducer the default mq producer
     */
    public void setDefaultMQProducer(final DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    @Override
    public void sendMessage(final String destination, final Integer pattern, final byte[] message) {
        try {
            Message msg;
            List<String> stringList = Splitter.on(CommonConstant.TOPIC_TAG_SEPARATOR).trimResults().splitToList(destination);
            if (stringList.size() > 1) {
                String topic = stringList.get(0);
                String tags = stringList.get(1);
                msg = new Message(topic, tags, message);
            } else {
                msg = new Message(destination, "", message);
            }
            final SendResult sendResult = defaultMQProducer.send(msg);
            LogUtil.info(LOGGER, sendResult::toString);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(LOGGER, e::getMessage);
            throw new MykitRuntimeException();
        }
    }
}
