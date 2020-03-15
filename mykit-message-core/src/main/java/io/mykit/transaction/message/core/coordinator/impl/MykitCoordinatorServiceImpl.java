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
package io.mykit.transaction.message.core.coordinator.impl;

import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.core.coordinator.MykitCoordinatorService;
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.service.MykitApplicationService;
import io.mykit.transaction.message.core.spi.MykitCoordinatorRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
@Service("coordinatorService")
public class MykitCoordinatorServiceImpl implements MykitCoordinatorService {

    private MykitCoordinatorRepository mykitCoordinatorRepository;

    private final MykitApplicationService mykitApplicationService;

    /**
     * Instantiates a new Mykit coordinator service.
     *
     * @param mykitApplicationService the rpc application service
     */
    @Autowired
    public MykitCoordinatorServiceImpl(final MykitApplicationService mykitApplicationService) {
        this.mykitApplicationService = mykitApplicationService;
    }

    @Override
    public void start(final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        mykitCoordinatorRepository = SpringBeanUtils.getInstance().getBean(MykitCoordinatorRepository.class);
        final String repositorySuffix = buildRepositorySuffix(mykitTransactionMessageConfig.getRepositorySuffix());
        //初始化spi 协调资源存储
        mykitCoordinatorRepository.init(repositorySuffix, mykitTransactionMessageConfig);
    }

    @Override
    public String save(final MykitTransaction mykitTransaction) {
        final int rows = mykitCoordinatorRepository.create(mykitTransaction);
        if (rows > 0) {
            return mykitTransaction.getTransId();
        }
        return null;
    }

    @Override
    public MykitTransaction findByTransId(final String transId) {
        return mykitCoordinatorRepository.findByTransId(transId);
    }

    @Override
    public List<MykitTransaction> listAllByDelay(final Date date) {
        return mykitCoordinatorRepository.listAllByDelay(date);
    }

    @Override
    public boolean remove(final String transId) {
        return mykitCoordinatorRepository.remove(transId) > 0;
    }

    @Override
    public int update(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        return mykitCoordinatorRepository.update(mythTransaction);
    }

    @Override
    public void updateFailTransaction(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        mykitCoordinatorRepository.updateFailTransaction(mythTransaction);
    }

    @Override
    public void updateParticipant(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        mykitCoordinatorRepository.updateParticipant(mythTransaction);
    }

    @Override
    public int updateStatus(final String transId, final Integer status) {
        return mykitCoordinatorRepository.updateStatus(transId, status);
    }

    private String buildRepositorySuffix(final String repositorySuffix) {
        if (StringUtils.isNoneBlank(repositorySuffix)) {
            return repositorySuffix;
        } else {
            return mykitApplicationService.acquireName();
        }
    }
}
