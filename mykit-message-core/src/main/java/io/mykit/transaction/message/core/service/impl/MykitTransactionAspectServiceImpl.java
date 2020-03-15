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
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.service.MykitTransactionAspectService;
import io.mykit.transaction.message.core.service.MykitTransactionFactoryService;
import io.mykit.transaction.message.core.service.MykitTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitTransactionAspectServiceImpl
 */
@Component
public class MykitTransactionAspectServiceImpl implements MykitTransactionAspectService {

    private final MykitTransactionFactoryService mykitTransactionFactoryService;

    /**
     * Instantiates a new Mykit transaction aspect service.
     *
     * @param mykitTransactionFactoryService the mykit transaction factory service
     */
    @Autowired
    public MykitTransactionAspectServiceImpl(final MykitTransactionFactoryService mykitTransactionFactoryService) {
        this.mykitTransactionFactoryService = mykitTransactionFactoryService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(final MykitTransactionMessageContext mythTransactionContext, final ProceedingJoinPoint point) throws Throwable {
        final Class clazz = mykitTransactionFactoryService.factoryOf(mythTransactionContext);
        final MykitTransactionHandler mythTransactionHandler = (MykitTransactionHandler) SpringBeanUtils.getInstance().getBean(clazz);
        return mythTransactionHandler.handler(point, mythTransactionContext);
    }
}
