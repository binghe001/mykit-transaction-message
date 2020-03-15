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
package io.mykit.transaction.message.core.concurrent.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitTransactionThreadFactory
 */
public class MykitTransactionThreadFactory implements ThreadFactory {

    private boolean daemon;

    private final ThreadGroup THREAD_GROUP = new ThreadGroup("MykitTransactionMessage");

    private final AtomicLong threadNumber = new AtomicLong(1);

    private final String namePrefix;

    private MykitTransactionThreadFactory(final String namePrefix, final boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    /**
     * create ThreadFactory.
     *
     * @param namePrefix namePrefix
     * @param daemon     daemon
     * @return ThreadFactory thread factory
     */
    public static ThreadFactory create(final String namePrefix, final boolean daemon) {
        return new MykitTransactionThreadFactory(namePrefix, daemon);
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        Thread thread = new Thread(THREAD_GROUP, runnable,
                THREAD_GROUP.getName() + "-" + namePrefix + "-" + threadNumber.getAndIncrement());
        thread.setDaemon(daemon);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
