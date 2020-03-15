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
package io.mykit.transaction.message.core.mediator;

import io.mykit.transaction.message.common.bean.context.MykitTransactionMessageContext;
import io.mykit.transaction.message.common.constant.CommonConstant;
import io.mykit.transaction.message.common.utils.GsonUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
public class RpcMediator {
    private static final RpcMediator RPC_MEDIATOR = new RpcMediator();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RpcMediator getInstance() {
        return RPC_MEDIATOR;
    }


    /**
     * Transmit.
     *
     * @param rpcTransmit the rpc mediator
     * @param context     the context
     */
    public void transmit(final RpcTransmit rpcTransmit, final MykitTransactionMessageContext context) {
        rpcTransmit.transmit(CommonConstant.MYKIT_TRANSACTION_MESSAGE_CONTEXT,
                GsonUtils.getInstance().toJson(context));
    }

    /**
     * Acquire hmily transaction context.
     *
     * @param rpcAcquire the rpc acquire
     * @return the hmily transaction context
     */
    public MykitTransactionMessageContext acquire(RpcAcquire rpcAcquire) {
        MykitTransactionMessageContext mythTransactionContext = null;
        final String context = rpcAcquire.acquire(CommonConstant.MYKIT_TRANSACTION_MESSAGE_CONTEXT);
        if (StringUtils.isNoneBlank(context)) {
            mythTransactionContext = GsonUtils.getInstance().fromJson(context, MykitTransactionMessageContext.class);
        }
        return mythTransactionContext;
    }
}
