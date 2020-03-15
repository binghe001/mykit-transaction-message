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
package io.mykit.transaction.message.core.schedule;

import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.enums.EventTypeEnum;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum;
import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.core.concurrent.threadpool.MykitTransactionThreadFactory;
import io.mykit.transaction.message.core.coordinator.MykitCoordinatorService;
import io.mykit.transaction.message.core.disruptor.publisher.MykitTransactionEventPublisher;
import io.mykit.transaction.message.core.service.MykitSendMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author binghe
 * @version 1.0.0
 * @description ScheduledService
 */
@Component
public class ScheduledService implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledService.class);

    private final MykitSendMessageService mykitSendMessageService;

    private final MykitCoordinatorService mykitCoordinatorService;

    private final MykitTransactionEventPublisher publisher;

    private final MykitTransactionMessageConfig mykitTransactionMessageConfig;

    @Autowired
    public ScheduledService(final MykitSendMessageService mykitSendMessageService,
                            final MykitCoordinatorService mykitCoordinatorService,
                            final MykitTransactionEventPublisher publisher,
                            final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        this.mykitSendMessageService = mykitSendMessageService;
        this.mykitCoordinatorService = mykitCoordinatorService;
        this.publisher = publisher;
        this.mykitTransactionMessageConfig = mykitTransactionMessageConfig;
    }

    /**
     * Scheduled auto recover.
     */
    private void scheduledAutoRecover() {
        new ScheduledThreadPoolExecutor(1, MykitTransactionThreadFactory.create("MykitAutoRecoverService", true))
                .scheduleWithFixedDelay(() -> {
                    LogUtil.debug(LOGGER, "auto recover execute delayTime:{}", mykitTransactionMessageConfig::getScheduledDelay);
                    try {
                        final List<MykitTransaction> mythTransactionList = mykitCoordinatorService.listAllByDelay(acquireData(mykitTransactionMessageConfig));
                        if (CollectionUtils.isNotEmpty(mythTransactionList)) {
                            mythTransactionList.forEach(mythTransaction -> {
                                final Boolean success = mykitSendMessageService.sendMessage(mythTransaction);
                                //发送成功 ，更改状态
                                if (success) {
                                    mythTransaction.setStatus(MykitTransactionMessageStatusEnum.COMMIT.getCode());
                                    publisher.publishEvent(mythTransaction, EventTypeEnum.UPDATE_STATUS.getCode());
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 30, mykitTransactionMessageConfig.getScheduledDelay(), TimeUnit.SECONDS);

    }

    private Date acquireData(final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        return new Date(LocalDateTime.now().atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli() - (mykitTransactionMessageConfig.getRecoverDelayTime() * 1000));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (mykitTransactionMessageConfig.getNeedRecover()) {
            scheduledAutoRecover();
        }
    }
}
