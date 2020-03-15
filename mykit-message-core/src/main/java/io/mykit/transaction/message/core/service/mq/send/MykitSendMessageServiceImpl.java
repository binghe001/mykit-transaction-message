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
package io.mykit.transaction.message.core.service.mq.send;

import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionParticipant;
import io.mykit.transaction.message.common.bean.mq.MessageEntity;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.service.MykitMqSendService;
import io.mykit.transaction.message.core.service.MykitSendMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitSendMessageServiceImpl
 */
@Service("mykitSendMessageService")
public class MykitSendMessageServiceImpl implements MykitSendMessageService {

    private volatile ObjectSerializer serializer;

    private volatile MykitMqSendService mykitMqSendService;

    @Override
    public Boolean sendMessage(final MykitTransaction mykitTransaction) {
        if (Objects.isNull(mykitTransaction)) {
            return false;
        }
        final List<MykitTransactionParticipant> mykitParticipants = mykitTransaction.getMykitTransactionParticipants();
        /*
         * 这里的这个判断很重要，不为空，表示本地的方法执行成功，需要执行远端的rpc方法
         * 为什么呢，因为我会在切面的finally里面发送消息，意思是切面无论如何都需要发送mq消息
         * 那么考虑问题，如果本地执行成功，调用rpc的时候才需要发
         * 如果本地异常，则不需要发送mq ，此时mythParticipants为空
         */
        if (CollectionUtils.isNotEmpty(mykitParticipants)) {
            for (MykitTransactionParticipant mythParticipant : mykitParticipants) {
                MessageEntity messageEntity = new MessageEntity(mythParticipant.getTransId(), mythParticipant.getMythInvocation());
                try {
                    final byte[] message = getObjectSerializer().serialize(messageEntity);
                    getMykitMqSendService().sendMessage(mythParticipant.getDestination(), mythParticipant.getPattern(), message);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    private synchronized MykitMqSendService getMykitMqSendService() {
        if (mykitMqSendService == null) {
            synchronized (MykitSendMessageServiceImpl.class) {
                if (mykitMqSendService == null) {
                    mykitMqSendService = SpringBeanUtils.getInstance().getBean(MykitMqSendService.class);
                }
            }
        }
        return mykitMqSendService;
    }

    private synchronized ObjectSerializer getObjectSerializer() {
        if (serializer == null) {
            synchronized (MykitSendMessageServiceImpl.class) {
                if (serializer == null) {
                    serializer = SpringBeanUtils.getInstance().getBean(ObjectSerializer.class);
                }
            }
        }
        return serializer;
    }
}
