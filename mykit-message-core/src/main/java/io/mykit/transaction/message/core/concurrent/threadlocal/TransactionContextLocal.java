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
package io.mykit.transaction.message.core.concurrent.threadlocal;

import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;

/**
 * @author binghe
 * @version 1.0.0
 * @description TransactionContextLocal
 */
public class TransactionContextLocal {

    private static final ThreadLocal<MykitTransactionMessageContext> CURRENT_LOCAL = new ThreadLocal<>();

    private static final TransactionContextLocal TRANSACTION_CONTEXT_LOCAL = new TransactionContextLocal();

    private TransactionContextLocal() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TransactionContextLocal getInstance() {
        return TRANSACTION_CONTEXT_LOCAL;
    }

    /**
     * Set.
     *
     * @param context the context
     */
    public void set(final MykitTransactionMessageContext context) {
        CURRENT_LOCAL.set(context);
    }

    /**
     * Get myth transaction context.
     *
     * @return the myth transaction context
     */
    public MykitTransactionMessageContext get() {
        return CURRENT_LOCAL.get();
    }

    /**
     * Remove.
     */
    public void remove() {
        CURRENT_LOCAL.remove();
    }
}
