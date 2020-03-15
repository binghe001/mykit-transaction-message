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
package io.mykit.transaction.message.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author binghe
 * @version 1.0.0
 * @description AbstractMykitTransactionAspect
 */
@Aspect
public abstract class AbstractMykitTransactionAspect {

    private MykitTransactionInterceptor mykitTransactionInterceptor;

    /**
     * set MythTransactionInterceptor.
     *
     * @param mykitTransactionInterceptor {@linkplain MykitTransactionInterceptor}
     */
    protected void setMythTransactionInterceptor(final MykitTransactionInterceptor mykitTransactionInterceptor) {
        this.mykitTransactionInterceptor = mykitTransactionInterceptor;
    }


    /**
     * this is point cut with {@linkplain io.mykit.transaction.message.annotation.MykitTransactionMessage }.
     */
    @Pointcut("@annotation(io.mykit.transaction.message.annotation.MykitTransactionMessage)")
    public void mykitTransactionInterceptor() {

    }

    /**
     * this is around in {@linkplain io.mykit.transaction.message.annotation.MykitTransactionMessage }.
     *
     * @param proceedingJoinPoint proceedingJoinPoint
     * @return Object object
     * @throws Throwable Throwable
     */
    @Around("mykitTransactionInterceptor()")
    public Object interceptMythAnnotationMethod(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return mykitTransactionInterceptor.interceptor(proceedingJoinPoint);
    }

    /**
     * spring bean Order.
     *
     * @return int order
     */
    public abstract int getOrder();
}
