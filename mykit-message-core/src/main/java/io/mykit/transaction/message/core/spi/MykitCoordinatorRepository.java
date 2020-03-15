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
package io.mykit.transaction.message.core.spi;

import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;

import java.util.Date;
import java.util.List;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitCoordinatorRepository
 */
@MykitTransactionMessageSPI
public interface MykitCoordinatorRepository {

    /**
     * create mykitTransaction.
     *
     * @param mykitTransaction {@linkplain MykitTransaction}
     * @return Influence row number
     */
    int create(MykitTransaction mykitTransaction);

    /**
     * delete mykitTransaction.
     *
     * @param transId pk
     * @return Influence row number
     */
    int remove(String transId);


    /**
     * update mykitTransaction. {@linkplain MykitTransaction}
     *
     * @param mythTransaction {@linkplain MykitTransaction}
     * @return Influence row number
     * @throws MykitRuntimeException ex {@linkplain MykitRuntimeException}
     */
    int update(MykitTransaction mythTransaction) throws MykitRuntimeException;


    /**
     * update fail info in mykitTransaction.
     *
     * @param mythTransaction {@linkplain MykitTransaction}
     * @throws MykitRuntimeException ex {@linkplain MykitRuntimeException}
     */
    void updateFailTransaction(MykitTransaction mythTransaction) throws MykitRuntimeException;


    /**
     * update participants in mykitTransaction.
     * this have only update this participant filed.
     *
     * @param mykitTransaction {@linkplain MykitTransaction}
     * @throws MykitRuntimeException ex {@linkplain MykitRuntimeException}
     */
    void updateParticipant(MykitTransaction mykitTransaction) throws MykitRuntimeException;


    /**
     * update status in mykitTransaction.
     *
     * @param transId pk
     * @param status  {@linkplain io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum}
     * @return Influence row number
     * @throws MykitRuntimeException ex {@linkplain MykitRuntimeException}
     */
    int updateStatus(String transId, Integer status) throws MykitRuntimeException;

    /**
     * find mykitTransaction by transId.
     *
     * @param transId pk
     * @return {@linkplain MykitTransaction}
     */
    MykitTransaction findByTransId(String transId);


    /**
     * list all mykitTransaction by delay date.
     *
     * @param date delay date
     * @return list mykitTransaction
     */
    List<MykitTransaction> listAllByDelay(Date date);


    /**
     * init CoordinatorRepository.
     *
     * @param modelName  model name
     * @param mykitTransactionMessageConfig {@linkplain MykitTransactionMessageConfig}
     * @throws MykitRuntimeException ex {@linkplain MykitRuntimeException}
     */
    void init(String modelName, MykitTransactionMessageConfig mykitTransactionMessageConfig) throws MykitRuntimeException;

    /**
     * set objectSerializer.
     *
     * @param objectSerializer {@linkplain ObjectSerializer}
     */
    void setSerializer(ObjectSerializer objectSerializer);
}
