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
import com.alibaba.dubbo.rpc.proxy.jdk.JdkProxyFactory;

import java.lang.reflect.Proxy;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitJdkProxyFactory
 */
public class MykitJdkProxyFactory extends JdkProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(final Invoker<T> invoker, final Class<?>[] interfaces) {
        T proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                interfaces, new InvokerInvocationHandler(invoker));
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                interfaces, new MykitInvokerInvocationHandler(proxy, invoker));
    }
}
