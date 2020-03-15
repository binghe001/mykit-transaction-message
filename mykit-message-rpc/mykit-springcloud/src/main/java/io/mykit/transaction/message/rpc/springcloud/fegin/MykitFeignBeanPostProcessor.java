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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitFeignBeanPostProcessor
 */
public class MykitFeignBeanPostProcessor  implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (!Proxy.isProxyClass(bean.getClass())) {
            return bean;
        }
        InvocationHandler handler = Proxy.getInvocationHandler(bean);

        final Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());

        for (Method method : methods) {
            MykitTransactionMessage mykitTransactionMessage = AnnotationUtils.findAnnotation(method, MykitTransactionMessage.class);
            if (Objects.nonNull(mykitTransactionMessage)) {
                MykitFeignHandler mythFeignHandler = new MykitFeignHandler();
                mythFeignHandler.setDelegate(handler);
                Class<?> clazz = bean.getClass();
                Class<?>[] interfaces = clazz.getInterfaces();
                ClassLoader loader = clazz.getClassLoader();
                return Proxy.newProxyInstance(loader, interfaces, mythFeignHandler);
            }
        }
        return bean;
    }

}
