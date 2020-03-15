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
package io.mykit.transaction.message.rpc.springcloud.interceptor;

import io.mykit.transaction.message.core.interceptor.AbstractMykitTransactionAspect;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
@Aspect
@Component
public class SpringCloudMykitTransactionAspect extends AbstractMykitTransactionAspect implements Ordered {

    /**
     * Instantiates a new Spring cloud myth transaction aspect.
     *
     * @param springCloudMykitTransactionInterceptor the spring cloud myth transaction interceptor
     */
    @Autowired
    public SpringCloudMykitTransactionAspect(final SpringCloudMykitTransactionInterceptor springCloudMykitTransactionInterceptor) {
        this.setMythTransactionInterceptor(springCloudMykitTransactionInterceptor);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

