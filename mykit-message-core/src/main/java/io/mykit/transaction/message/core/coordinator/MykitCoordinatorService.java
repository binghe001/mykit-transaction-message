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
package io.mykit.transaction.message.core.coordinator;

import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.exception.MykitException;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;

import java.util.Date;
import java.util.List;

/**
 * @author binghe
 * @version 1.0.0
 * @description MythCoordinatorService
 */
public interface MykitCoordinatorService {

    /**
     * start coordinator service.
     *
     * @param mykitTransactionMessageConfig {@linkplain MykitTransactionMessageConfig}
     * @throws MykitException ex
     */
    void start(MykitTransactionMessageConfig mykitTransactionMessageConfig) throws MykitException;

    /**
     * save MythTransaction.
     *
     * @param mykitTransaction {@linkplain MykitTransaction}
     * @return pk
     */
    String save(MykitTransaction mykitTransaction);

    /**
     * find MythTransaction by id.
     *
     * @param transId pk
     * @return {@linkplain MykitTransaction}
     */
    MykitTransaction findByTransId(String transId);


    /**
     * find  MythTransaction by Delay Date.
     *
     * @param date delay date
     * @return {@linkplain MykitTransaction}
     */
    List<MykitTransaction> listAllByDelay(Date date);

    /**
     * delete MythTransaction.
     *
     * @param transId pk
     * @return true  false
     */
    boolean remove(String transId);

    /**
     * update  MythTransaction.
     *
     * @param mykitTransaction {@linkplain MykitTransaction}
     * @return rows 1
     * @throws MykitRuntimeException ex
     */
    int update(MykitTransaction mykitTransaction) throws MykitRuntimeException;


    /**
     * update fail info.
     * @param mykitTransaction {@linkplain MykitTransaction}
     * @throws MykitRuntimeException ex
     */
    void updateFailTransaction(MykitTransaction mykitTransaction) throws MykitRuntimeException;

    /**
     * update Participant.
     * @param mykitTransaction {@linkplain MykitTransaction}
     * @throws MykitRuntimeException ex
     */
    void updateParticipant(MykitTransaction mykitTransaction) throws MykitRuntimeException;

    /**
     * update status.
     * @param transId pk
     * @param status  status
     * @return rows 1
     * @throws MykitRuntimeException ex
     */
    int updateStatus(String transId, Integer status) throws MykitRuntimeException;
}
