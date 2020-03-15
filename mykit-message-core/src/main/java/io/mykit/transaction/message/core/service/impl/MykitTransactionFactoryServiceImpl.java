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
package io.mykit.transaction.message.core.service.impl;

import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.core.service.MykitTransactionFactoryService;
import io.mykit.transaction.message.core.service.engine.MykitTransactionEngine;
import io.mykit.transaction.message.core.service.handler.ActorMykitTransactionHandler;
import io.mykit.transaction.message.core.service.handler.LocalMykitTransactionHandler;
import io.mykit.transaction.message.core.service.handler.StartMykitTransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitTransactionFactoryServiceImpl
 */
@Service
public class MykitTransactionFactoryServiceImpl implements MykitTransactionFactoryService {

    private final MykitTransactionEngine mykitTransactionEngine;

    /**
     * Instantiates a new Mykit transaction factory service.
     *
     * @param mykitTransactionEngine the mykit transaction engine
     */
    @Autowired
    public MykitTransactionFactoryServiceImpl(final MykitTransactionEngine mykitTransactionEngine) {
        this.mykitTransactionEngine = mykitTransactionEngine;
    }

    @Override
    public Class factoryOf(final MykitTransactionMessageContext context) {
        if (Objects.isNull(context)) {
            //上下文不为空，并且当前事务存在threadLocal里面，那只可能是内嵌调用，或者走了多个切面
            if (mykitTransactionEngine.isBegin()) {
                return LocalMykitTransactionHandler.class;
            }
            return StartMykitTransactionHandler.class;
        } else {
            return ActorMykitTransactionHandler.class;
        }
    }
}
