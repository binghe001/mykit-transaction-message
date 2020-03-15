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
package io.mykit.transaction.message.rpc.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.core.concurrent.threadlocal.TransactionContextLocal;
import io.mykit.transaction.message.core.mediator.RpcMediator;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author binghe
 * @version 1.0.0
 * @description DubboMykitTransactionFilter
 */
@Activate(group = {Constants.SERVER_KEY, Constants.CONSUMER})
public class DubboMykitTransactionFilter implements Filter {

    @Override
    @SuppressWarnings("unchecked")
    public Result invoke(final Invoker<?> invoker, final Invocation invocation) throws RpcException {
        String methodName = invocation.getMethodName();
        Class clazz = invoker.getInterface();
        Class[] args = invocation.getParameterTypes();
        Method method;
        MykitTransactionMessage mykitTransactionMessage = null;
        try {
            method = clazz.getDeclaredMethod(methodName, args);
            mykitTransactionMessage = method.getAnnotation(MykitTransactionMessage.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (Objects.nonNull(mykitTransactionMessage)) {
            final MykitTransactionMessageContext mykitTransactionMessageContext = TransactionContextLocal.getInstance().get();
            if (Objects.nonNull(mykitTransactionMessageContext)) {
                RpcMediator.getInstance().transmit(RpcContext.getContext()::setAttachment, mykitTransactionMessageContext);
            }
        }
        return invoker.invoke(invocation);
    }
}
