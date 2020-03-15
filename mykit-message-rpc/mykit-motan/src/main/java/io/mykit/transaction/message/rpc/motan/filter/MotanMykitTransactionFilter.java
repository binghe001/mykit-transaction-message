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
package io.mykit.transaction.message.rpc.motan.filter;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.DefaultResponse;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.util.ReflectUtil;
import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionInvocation;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionParticipant;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.core.concurrent.threadlocal.TransactionContextLocal;
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.mediator.RpcMediator;
import io.mykit.transaction.message.core.service.engine.MykitTransactionEngine;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author binghe
 * @version 1.0.0
 * @description MotanMykitTransactionFilter
 */
@SpiMeta(name = "motanMykitTransactionFilter")
@Activation(key = {MotanConstants.NODE_TYPE_REFERER})
public class MotanMykitTransactionFilter implements Filter {

    @Override
    @SuppressWarnings("unchecked")
    public Response filter(final Caller<?> caller, final Request request) {
        final String interfaceName = request.getInterfaceName();
        final String methodName = request.getMethodName();
        final Object[] arguments = request.getArguments();
        Class[] args = null;
        Method method = null;
        MykitTransactionMessage mykitTransactionMessage = null;
        Class clazz = null;
        try {
            //他妈的 这里还要拿方法参数类型
            clazz = ReflectUtil.forName(interfaceName);
            final Method[] methods = clazz.getMethods();
            args = Stream.of(methods)
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst()
                    .map(Method::getParameterTypes).get();
            method = clazz.getDeclaredMethod(methodName, args);
            mykitTransactionMessage = method.getAnnotation(MykitTransactionMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.nonNull(mykitTransactionMessage)) {
            final MykitTransactionMessageContext mykitTransactionMessageContext = TransactionContextLocal.getInstance().get();
            if (Objects.nonNull(mykitTransactionMessageContext)) {
                RpcMediator.getInstance().transmit(request::setAttachment,mykitTransactionMessageContext);
            }
            final MykitTransactionParticipant participant =
                    buildParticipant(mykitTransactionMessageContext, mykitTransactionMessage,
                            method, clazz, arguments, args);
            if (Objects.nonNull(participant)) {
                SpringBeanUtils.getInstance().getBean(MykitTransactionEngine.class).registerParticipant(participant);
            }
            try {
                return caller.call(request);
            } catch (Exception e) {
                e.printStackTrace();
                return new DefaultResponse();
            }
        } else {
            return caller.call(request);
        }
    }

    private MykitTransactionParticipant buildParticipant(final MykitTransactionMessageContext mykitTransactionMessageContext,
                                             final MykitTransactionMessage myth, final Method method,
                                             final Class clazz, final Object[] arguments,
                                             final Class... args) throws MykitRuntimeException {
        if (Objects.nonNull(mykitTransactionMessageContext)) {
            if (Objects.isNull(method) || (Objects.isNull(clazz))) {
                return null;
            }
            MykitTransactionInvocation mythInvocation = new MykitTransactionInvocation(clazz, method.getName(), args, arguments);

            final String destination = myth.destination();

            final Integer pattern = myth.pattern().getCode();
            //封装调用点
            return new MykitTransactionParticipant(mykitTransactionMessageContext.getTransId(), destination, pattern, mythInvocation);
        }
        return null;
    }
}
