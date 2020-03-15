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
package io.mykit.transaction.message.core.disruptor.publisher;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.core.concurrent.threadpool.MykitTransactionThreadFactory;
import io.mykit.transaction.message.core.coordinator.MykitCoordinatorService;
import io.mykit.transaction.message.core.disruptor.event.MykitTransactionEvent;
import io.mykit.transaction.message.core.disruptor.factory.MykitTransactionEventFactory;
import io.mykit.transaction.message.core.disruptor.handler.MykitTransactionEventHandler;
import io.mykit.transaction.message.core.disruptor.translator.MykitTransactionEventTranslator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
@Component
public class MykitTransactionEventPublisher implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {
    private static final int MAX_THREAD = Runtime.getRuntime().availableProcessors() << 1;

    private static final AtomicLong INDEX = new AtomicLong(1);

    private Disruptor<MykitTransactionEvent> disruptor;

    private final MykitCoordinatorService coordinatorService;

    private final MykitTransactionMessageConfig mykitTransactionMessageConfig;

    @Autowired
    public MykitTransactionEventPublisher(MykitCoordinatorService coordinatorService, MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        this.coordinatorService = coordinatorService;
        this.mykitTransactionMessageConfig = mykitTransactionMessageConfig;
    }

    /**
     * start disruptor.
     *
     * @param bufferSize bufferSize
     */
    private void start(final int bufferSize, final int threadSize) {
        disruptor = new Disruptor<>(new MykitTransactionEventFactory(), bufferSize, runnable -> {
            return new Thread(new ThreadGroup("mykit-disruptor"), runnable,
                    "disruptor-thread-" + INDEX.getAndIncrement());
        }, ProducerType.MULTI, new BlockingWaitStrategy());
        final Executor executor = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                MykitTransactionThreadFactory.create("mykit-log-disruptor", false),
                new ThreadPoolExecutor.AbortPolicy());

        MykitTransactionEventHandler[] consumers = new MykitTransactionEventHandler[MAX_THREAD];
        for (int i = 0; i < threadSize; i++) {
            consumers[i] = new MykitTransactionEventHandler(coordinatorService, executor);
        }
        disruptor.handleEventsWithWorkerPool(consumers);
        disruptor.setDefaultExceptionHandler(new IgnoreExceptionHandler());
        disruptor.start();
    }


    /**
     * publish disruptor event.
     *
     * @param mykitTransaction {@linkplain MykitTransaction }
     * @param type            {@linkplain io.mykit.transaction.message.common.enums.EventTypeEnum}
     */
    public void publishEvent(final MykitTransaction mykitTransaction, final int type) {
        final RingBuffer<MykitTransactionEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(new MykitTransactionEventTranslator(type), mykitTransaction);
    }

    @Override
    public void destroy() {
        disruptor.shutdown();
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        start(mykitTransactionMessageConfig.getBufferSize(), mykitTransactionMessageConfig.getConsumerThreads());
    }
}
