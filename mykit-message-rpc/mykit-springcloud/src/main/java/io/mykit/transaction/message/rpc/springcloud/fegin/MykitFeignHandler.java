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
package io.mykit.transaction.message.rpc.springcloud.fegin;

import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionInvocation;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionParticipant;
import io.mykit.transaction.message.common.utils.DefaultValueUtils;
import io.mykit.transaction.message.core.concurrent.threadlocal.TransactionContextLocal;
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.service.engine.MykitTransactionEngine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitFeignHandler
 */
public class MykitFeignHandler  implements InvocationHandler {

    private InvocationHandler delegate;

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else {
            final MykitTransactionMessage mykitTransactionMessage = method.getAnnotation(MykitTransactionMessage.class);
            if (Objects.isNull(mykitTransactionMessage)) {
                return this.delegate.invoke(proxy, method, args);
            }
            try {
                final MykitTransactionEngine mykitTransactionEngine =
                        SpringBeanUtils.getInstance().getBean(MykitTransactionEngine.class);
                final MykitTransactionParticipant participant = buildParticipant(mykitTransactionMessage, method, args);
                if (Objects.nonNull(participant)) {
                    mykitTransactionEngine.registerParticipant(participant);
                }
                return this.delegate.invoke(proxy, method, args);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return DefaultValueUtils.getDefaultValue(method.getReturnType());
            }
        }
    }

    private MykitTransactionParticipant buildParticipant(final MykitTransactionMessage mykitTransactionMessage, final Method method, final Object[] args) {
        final MykitTransactionMessageContext mykitTransactionMessageContext = TransactionContextLocal.getInstance().get();

        MykitTransactionParticipant participant;
        if (Objects.nonNull(mykitTransactionMessageContext)) {
            final Class declaringClass = mykitTransactionMessage.target();
            MykitTransactionInvocation mykitTransactionInvocation =
                    new MykitTransactionInvocation(declaringClass, method.getName(), method.getParameterTypes(), args);
            final Integer pattern = mykitTransactionMessage.pattern().getCode();
            //封装调用点
            participant = new MykitTransactionParticipant(mykitTransactionMessageContext.getTransId(),
                    mykitTransactionMessage.destination(),
                    pattern,
                    mykitTransactionInvocation);
            return participant;
        }
        return null;
    }

    public void setDelegate(InvocationHandler delegate) {
        this.delegate = delegate;
    }
}
