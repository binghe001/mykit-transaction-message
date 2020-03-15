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
package io.mykit.transaction.message.core.service.engine;

import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionParticipant;
import io.mykit.transaction.message.common.enums.EventTypeEnum;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageRoleEnum;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum;
import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.core.concurrent.threadlocal.TransactionContextLocal;
import io.mykit.transaction.message.core.disruptor.publisher.MykitTransactionEventPublisher;
import io.mykit.transaction.message.core.service.MykitSendMessageService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitTransactionEngine
 */
@Component
@SuppressWarnings("unchecked")
public class MykitTransactionEngine {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MykitTransactionEngine.class);

    /**
     * save MykitTransaction in threadLocal.
     */
    private static final ThreadLocal<MykitTransaction> CURRENT = new ThreadLocal<>();

    private final MykitSendMessageService mykitSendMessageService;

    private final MykitTransactionEventPublisher publishEvent;

    /**
     * Instantiates a new Mykit transaction engine.
     *
     * @param mykitSendMessageService the Mykit send message service
     * @param publishEvent           the publish event
     */
    @Autowired
    public MykitTransactionEngine(MykitSendMessageService mykitSendMessageService, MykitTransactionEventPublisher publishEvent) {
        this.mykitSendMessageService = mykitSendMessageService;
        this.publishEvent = publishEvent;
    }

    /**
     * this is stater begin MykitTransaction.
     *
     * @param point cut point.
     */
    public void begin(final ProceedingJoinPoint point) {
        LogUtil.debug(LOGGER, () -> "开始执行Mykit分布式事务！start");
        MykitTransaction MykitTransaction = buildMykitTransaction(point, MykitTransactionMessageRoleEnum.START.getCode(), MykitTransactionMessageStatusEnum.BEGIN.getCode(), "");
        //发布事务保存事件，异步保存
        publishEvent.publishEvent(MykitTransaction, EventTypeEnum.SAVE.getCode());
        //当前事务保存到ThreadLocal
        CURRENT.set(MykitTransaction);
        //设置tcc事务上下文，这个类会传递给远端
        MykitTransactionMessageContext context = new MykitTransactionMessageContext();
        //设置事务id
        context.setTransId(MykitTransaction.getTransId());
        //设置为发起者角色
        context.setRole(MykitTransactionMessageRoleEnum.START.getCode());
        TransactionContextLocal.getInstance().set(context);
    }

    /**
     * save errorMsg into MykitTransaction .
     *
     * @param errorMsg errorMsg
     */
    public void failTransaction(final String errorMsg) {
        MykitTransaction MykitTransaction = getCurrentTransaction();
        if (Objects.nonNull(MykitTransaction)) {
            MykitTransaction.setStatus(MykitTransactionMessageStatusEnum.FAILURE.getCode());
            MykitTransaction.setErrorMsg(errorMsg);
            publishEvent.publishEvent(MykitTransaction, EventTypeEnum.UPDATE_FAIR.getCode());
        }
    }

    /**
     * this is actor begin transaction.
     *
     * @param point                  cut point
     * @param MykitTransactionContext {@linkplain MykitTransactionMessageContext}
     */
    public void actorTransaction(final ProceedingJoinPoint point, final MykitTransactionMessageContext MykitTransactionContext) {
        MykitTransaction MykitTransaction =
                buildMykitTransaction(point, MykitTransactionMessageRoleEnum.PROVIDER.getCode(),
                        MykitTransactionMessageStatusEnum.BEGIN.getCode(), MykitTransactionContext.getTransId());
        //发布事务保存事件，异步保存
        publishEvent.publishEvent(MykitTransaction, EventTypeEnum.SAVE.getCode());
        //当前事务保存到ThreadLocal
        CURRENT.set(MykitTransaction);
        //设置提供者角色
        MykitTransactionContext.setRole(MykitTransactionMessageRoleEnum.PROVIDER.getCode());
        TransactionContextLocal.getInstance().set(MykitTransactionContext);
    }

    /**
     * update transaction status.
     *
     * @param status {@linkplain MykitTransactionMessageStatusEnum}
     */
    public void updateStatus(final int status) {
        MykitTransaction MykitTransaction = getCurrentTransaction();
        Optional.ofNullable(MykitTransaction)
                .map(t -> {
                    t.setStatus(status);
                    return t;
                }).ifPresent(t -> publishEvent.publishEvent(t, EventTypeEnum.UPDATE_STATUS.getCode()));
        MykitTransaction.setStatus(MykitTransactionMessageStatusEnum.COMMIT.getCode());
    }

    /**
     * send message.
     */
    public void sendMessage() {
        Optional.ofNullable(getCurrentTransaction()).ifPresent(mykitSendMessageService::sendMessage);
    }

    /**
     * transaction is begin.
     *
     * @return true boolean
     */
    public boolean isBegin() {
        return CURRENT.get() != null;
    }

    /**
     * help gc.
     */
    public void cleanThreadLocal() {
        CURRENT.remove();
    }

    private MykitTransaction getCurrentTransaction() {
        return CURRENT.get();
    }

    /**
     * add participant into transaction.
     *
     * @param participant {@linkplain MykitTransactionParticipant}
     */
    public void registerParticipant(final MykitTransactionParticipant participant) {
        final MykitTransaction MykitTransaction = this.getCurrentTransaction();
        MykitTransaction.registerParticipant(participant);
        publishEvent.publishEvent(MykitTransaction, EventTypeEnum.UPDATE_PARTICIPANT.getCode());
    }

    private MykitTransaction buildMykitTransaction(final ProceedingJoinPoint point, final int role,
                                                 final int status, final String transId) {
        MykitTransaction MykitTransaction;
        if (StringUtils.isNoneBlank(transId)) {
            MykitTransaction = new MykitTransaction(transId);
        } else {
            MykitTransaction = new MykitTransaction();
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        MykitTransaction.setStatus(status);
        MykitTransaction.setRole(role);
        MykitTransaction.setTargetClass(clazz.getName());
        MykitTransaction.setTargetMethod(method.getName());
        return MykitTransaction;
    }
}
