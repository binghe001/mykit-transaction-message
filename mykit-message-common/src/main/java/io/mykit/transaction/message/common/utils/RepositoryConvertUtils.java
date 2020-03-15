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
package io.mykit.transaction.message.common.utils;

import io.mykit.transaction.message.common.bean.adapter.CoordinatorRepositoryAdapter;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionParticipant;
import io.mykit.transaction.message.common.exception.MykitException;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author binghe
 * @version 1.0.0
 * @description RepositoryConvertUtils
 */
public class RepositoryConvertUtils {
    /**
     * Convert byte [ ].
     *
     * @param mythTransaction  the mykit transaction
     * @param objectSerializer the object serializer
     * @return the byte [ ]
     * @throws MykitException the myth exception
     */
    public static byte[] convert(final MykitTransaction mythTransaction, final ObjectSerializer objectSerializer) throws MykitException {
        CoordinatorRepositoryAdapter adapter = new CoordinatorRepositoryAdapter();
        adapter.setTransId(mythTransaction.getTransId());
        adapter.setLastTime(mythTransaction.getLastTime());
        adapter.setCreateTime(mythTransaction.getCreateTime());
        adapter.setRetriedCount(mythTransaction.getRetriedCount());
        adapter.setStatus(mythTransaction.getStatus());
        adapter.setTargetClass(mythTransaction.getTargetClass());
        adapter.setTargetMethod(mythTransaction.getTargetMethod());
        adapter.setRole(mythTransaction.getRole());
        adapter.setErrorMsg(mythTransaction.getErrorMsg());
        adapter.setVersion(mythTransaction.getVersion());
        adapter.setContents(objectSerializer.serialize(mythTransaction.getMykitTransactionParticipants()));
        return objectSerializer.serialize(adapter);
    }

    /**
     * Transform bean myth transaction.
     *
     * @param contents         the contents
     * @param objectSerializer the object serializer
     * @return the mykit transaction
     * @throws MykitException the myth exception
     */
    @SuppressWarnings("unchecked")
    public static MykitTransaction transformBean(final byte[] contents, final ObjectSerializer objectSerializer) throws MykitException {
        MykitTransaction mythTransaction = new MykitTransaction();
        final CoordinatorRepositoryAdapter adapter = objectSerializer.deSerialize(contents, CoordinatorRepositoryAdapter.class);
        List<MykitTransactionParticipant> participants = objectSerializer.deSerialize(adapter.getContents(), ArrayList.class);
        mythTransaction.setLastTime(adapter.getLastTime());
        mythTransaction.setRetriedCount(adapter.getRetriedCount());
        mythTransaction.setCreateTime(adapter.getCreateTime());
        mythTransaction.setTransId(adapter.getTransId());
        mythTransaction.setStatus(adapter.getStatus());
        mythTransaction.setMykitTransactionParticipants(participants);
        mythTransaction.setRole(adapter.getRole());
        mythTransaction.setTargetClass(adapter.getTargetClass());
        mythTransaction.setTargetMethod(adapter.getTargetMethod());
        mythTransaction.setVersion(adapter.getVersion());
        return mythTransaction;
    }
}
