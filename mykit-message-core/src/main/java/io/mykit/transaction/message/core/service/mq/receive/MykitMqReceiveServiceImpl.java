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
package io.mykit.transaction.message.core.service.mq.receive;

import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionInvocation;
import io.mykit.transaction.message.common.bean.mq.MessageEntity;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.enums.EventTypeEnum;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageRoleEnum;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum;
import io.mykit.transaction.message.common.exception.MykitException;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;
import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.core.concurrent.threadlocal.TransactionContextLocal;
import io.mykit.transaction.message.core.coordinator.MykitCoordinatorService;
import io.mykit.transaction.message.core.disruptor.publisher.MykitTransactionEventPublisher;
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.service.MykitMqReceiveService;
import io.mykit.transaction.message.core.service.mq.send.MykitSendMessageServiceImpl;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitMqReceiveServiceImpl
 */

@Service("mykitMqReceiveService")
public class MykitMqReceiveServiceImpl implements MykitMqReceiveService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MykitMqReceiveServiceImpl.class);

    private static final Lock LOCK = new ReentrantLock();

    private volatile ObjectSerializer serializer;

    private final MykitCoordinatorService mykitCoordinatorService;

    private final MykitTransactionEventPublisher publisher;

    private final MykitTransactionMessageConfig mykitTransactionMessageConfig;

    @Autowired
    public MykitMqReceiveServiceImpl(MykitCoordinatorService mykitCoordinatorService, MykitTransactionEventPublisher publisher, MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        this.mykitCoordinatorService = mykitCoordinatorService;
        this.publisher = publisher;
        this.mykitTransactionMessageConfig = mykitTransactionMessageConfig;
    }

    @Override
    public Boolean processMessage(final byte[] message) {
        try {
            MessageEntity entity;
            try {
                entity = getObjectSerializer().deSerialize(message, MessageEntity.class);
            } catch (MykitException e) {
                e.printStackTrace();
                throw new MykitRuntimeException(e.getMessage());
            }
            /*
             * 1 检查该事务有没被处理过，已经处理过的 则不处理
             * 2 发起发射调用，调用接口，进行处理
             * 3 记录本地日志
             */
            LOCK.lock();
            final String transId = entity.getTransId();
            final MykitTransaction mythTransaction = mykitCoordinatorService.findByTransId(transId);
            //第一次调用 也就是服务down机，或者没有调用到的时候， 通过mq执行
            if (Objects.isNull(mythTransaction)) {
                try {
                    execute(entity);
                    //执行成功 保存成功的日志
                    final MykitTransaction log = buildTransactionLog(transId, "",
                            MykitTransactionMessageStatusEnum.COMMIT.getCode(),
                            entity.getMykitTransactionMessageInvocation().getTargetClass().getName(),
                            entity.getMykitTransactionMessageInvocation().getMethodName());
                    //submit(new CoordinatorAction(CoordinatorActionEnum.SAVE, log));
                    publisher.publishEvent(log, EventTypeEnum.SAVE.getCode());
                } catch (Exception e) {
                    //执行失败保存失败的日志
                    final MykitTransaction log = buildTransactionLog(transId, getExceptionMessage(e),
                            MykitTransactionMessageStatusEnum.FAILURE.getCode(),
                            entity.getMykitTransactionMessageInvocation().getTargetClass().getName(),
                            entity.getMykitTransactionMessageInvocation().getMethodName());
                    publisher.publishEvent(log, EventTypeEnum.SAVE.getCode());
                    throw new MykitRuntimeException(e);
                } finally {
                    TransactionContextLocal.getInstance().remove();
                }
            } else {
                //如果是执行失败的话
                if (mythTransaction.getStatus() == MykitTransactionMessageStatusEnum.FAILURE.getCode()) {
                    //如果超过了最大重试次数 则不执行
                    if (mythTransaction.getRetriedCount() >= mykitTransactionMessageConfig.getRetryMax()) {
                        LogUtil.error(LOGGER, () -> "此事务已经超过了最大重试次数:" + mykitTransactionMessageConfig.getRetryMax()
                                + " ,执行接口为:" + entity.getMykitTransactionMessageInvocation().getTargetClass() + " ,方法为:"
                                + entity.getMykitTransactionMessageInvocation().getMethodName() + ",事务id为：" + entity.getTransId());
                        return Boolean.FALSE;
                    }
                    try {
                        execute(entity);
                        //执行成功 更新日志为成功
                        mythTransaction.setStatus(MykitTransactionMessageStatusEnum.COMMIT.getCode());
                        publisher.publishEvent(mythTransaction, EventTypeEnum.UPDATE_STATUS.getCode());

                    } catch (Throwable e) {
                        //执行失败，设置失败原因和重试次数
                        mythTransaction.setErrorMsg(getExceptionMessage(e));
                        mythTransaction.setRetriedCount(mythTransaction.getRetriedCount() + 1);
                        publisher.publishEvent(mythTransaction, EventTypeEnum.UPDATE_FAIR.getCode());
                        throw new MykitRuntimeException(e);
                    } finally {
                        TransactionContextLocal.getInstance().remove();
                    }
                }
            }

        } finally {
            LOCK.unlock();
        }
        return Boolean.TRUE;
    }

    private void execute(final MessageEntity entity) throws Exception {
        //设置事务上下文，这个类会传递给远端
        MykitTransactionMessageContext context = new MykitTransactionMessageContext();
        //设置事务id
        context.setTransId(entity.getTransId());
        //设置为发起者角色
        context.setRole(MykitTransactionMessageRoleEnum.LOCAL.getCode());
        TransactionContextLocal.getInstance().set(context);
        executeLocalTransaction(entity.getMykitTransactionMessageInvocation());
    }

    @SuppressWarnings("unchecked")
    private void executeLocalTransaction(final MykitTransactionInvocation mykitTransactionInvocation) throws Exception {
        if (Objects.nonNull(mykitTransactionInvocation)) {
            final Class clazz = mykitTransactionInvocation.getTargetClass();
            final String method = mykitTransactionInvocation.getMethodName();
            final Object[] args = mykitTransactionInvocation.getArgs();
            final Class[] parameterTypes = mykitTransactionInvocation.getParameterTypes();
            final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
            MethodUtils.invokeMethod(bean, method, args, parameterTypes);
            LogUtil.debug(LOGGER, "Mykit执行本地协调事务:{}", () -> mykitTransactionInvocation.getTargetClass() + ":" + mykitTransactionInvocation.getMethodName());

        }
    }

    private MykitTransaction buildTransactionLog(final String transId, final String errorMsg, final Integer status,
                                                final String targetClass, final String targetMethod) {
        MykitTransaction logTransaction = new MykitTransaction(transId);
        logTransaction.setRetriedCount(1);
        logTransaction.setStatus(status);
        logTransaction.setErrorMsg(errorMsg);
        logTransaction.setRole(MykitTransactionMessageRoleEnum.PROVIDER.getCode());
        logTransaction.setTargetClass(targetClass);
        logTransaction.setTargetMethod(targetMethod);
        return logTransaction;
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

    /**
     * 获取异常的详情
     * @param e
     * @return
     */
    private String getExceptionMessage(Throwable e) {
        String exceptionMessage = e.getMessage();
        if (exceptionMessage == null && e instanceof InvocationTargetException && e.getCause() != null) {
            exceptionMessage = e.getCause().getMessage();
        }
        return exceptionMessage;
    }
}
