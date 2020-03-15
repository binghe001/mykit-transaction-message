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
package io.mykit.transaction.message.rpc.dubbo.interceptor;

import com.alibaba.dubbo.rpc.RpcContext;
import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.core.interceptor.MykitTransactionInterceptor;
import io.mykit.transaction.message.core.mediator.RpcMediator;
import io.mykit.transaction.message.core.service.MykitTransactionAspectService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author binghe
 * @version 1.0.0
 * @description DubboMykitTransactionInterceptor
 */
@Component
public class DubboMykitTransactionInterceptor implements MykitTransactionInterceptor {
    private final MykitTransactionAspectService mykitTransactionAspectService;

    /**
     * Instantiates a new Dubbo mykit transaction interceptor.
     *
     * @param mykitTransactionAspectService the mykit transaction aspect service
     */
    @Autowired
    public DubboMykitTransactionInterceptor(final MykitTransactionAspectService mykitTransactionAspectService) {
        this.mykitTransactionAspectService = mykitTransactionAspectService;
    }

    @Override
    public Object interceptor(final ProceedingJoinPoint pjp) throws Throwable {
        MykitTransactionMessageContext mykitTransactionMessageContext =
                RpcMediator.getInstance().acquire(RpcContext.getContext()::getAttachment);
        return mykitTransactionAspectService.invoke(mykitTransactionMessageContext, pjp);
    }
}
