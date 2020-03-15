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
package io.mykit.transaction.message.rpc.dubbo.proxy;

import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;
import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionInvocation;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionParticipant;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.utils.DefaultValueUtils;
import io.mykit.transaction.message.core.concurrent.threadlocal.TransactionContextLocal;
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.service.engine.MykitTransactionEngine;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitInvokerInvocationHandler
 */
public class MykitInvokerInvocationHandler extends InvokerInvocationHandler {

    private Object target;

    /**
     * Instantiates a new Myth invoker invocation handler.
     *
     * @param handler the handler
     */
    public MykitInvokerInvocationHandler(final Invoker<?> handler) {
        super(handler);
    }

    /**
     * Instantiates a new Myth invoker invocation handler.
     *
     * @param target  the target
     * @param invoker the invoker
     */
    public <T> MykitInvokerInvocationHandler(final T target, final Invoker<T> invoker) {
        super(invoker);
        this.target = target;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final MykitTransactionMessage mykitTransactionMessage = method.getAnnotation(MykitTransactionMessage.class);
        final Class<?>[] arguments = method.getParameterTypes();
        final Class clazz = method.getDeclaringClass();
        if (Objects.nonNull(mykitTransactionMessage)) {
            final MykitTransactionMessageContext mykitTransactionMessageContext = TransactionContextLocal.getInstance().get();
            try {
                final MykitTransactionParticipant participant =
                        buildParticipant(mykitTransactionMessageContext, mykitTransactionMessage,  method, clazz, args, arguments);
                if (Objects.nonNull(participant)) {
                    final MykitTransactionEngine mykitTransactionEngine =
                            SpringBeanUtils.getInstance().getBean(MykitTransactionEngine.class);
                    mykitTransactionEngine.registerParticipant(participant);
                }
                return super.invoke(target, method, args);
            } catch (Throwable throwable) {
                //todo 需要记录下错误日志
                throwable.printStackTrace();
                return DefaultValueUtils.getDefaultValue(method.getReturnType());
            }
        } else {
            return super.invoke(target, method, args);
        }
    }

    private MykitTransactionParticipant buildParticipant(final MykitTransactionMessageContext mykitTransactionMessageContext,
                                             final MykitTransactionMessage mykitTransactionMessage, final Method method,
                                             final Class clazz, final Object[] arguments,
                                             final Class... args) throws MykitRuntimeException {
        if (Objects.nonNull(mykitTransactionMessageContext)) {
            MykitTransactionInvocation mykitTransactionInvocation = new MykitTransactionInvocation(clazz, method.getName(), args, arguments);
            //有tags的消息队列的特殊处理
            final String destination;
            if (mykitTransactionMessage.tags().length() > 0) {
                destination = mykitTransactionMessage.destination() + "," + mykitTransactionMessage.tags();
            } else {
                destination = mykitTransactionMessage.destination();
            }
            final Integer pattern = mykitTransactionMessage.pattern().getCode();
            //封装调用点
            return new MykitTransactionParticipant(mykitTransactionMessageContext.getTransId(), destination, pattern, mykitTransactionInvocation);
        }
        return null;
    }
}
