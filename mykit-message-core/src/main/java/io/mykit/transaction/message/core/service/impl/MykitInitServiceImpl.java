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
package io.mykit.transaction.message.core.service.impl;

import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;
import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.common.utils.extension.ExtensionLoader;
import io.mykit.transaction.message.core.coordinator.MykitCoordinatorService;
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.logo.MykitLogo;
import io.mykit.transaction.message.core.service.MykitInitService;
import io.mykit.transaction.message.core.spi.MykitCoordinatorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitInitServiceImpl
 */
@Service
public class MykitInitServiceImpl implements MykitInitService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MykitInitServiceImpl.class);

    private final MykitCoordinatorService mykitCoordinatorService;

    /**
     * Instantiates a new Mykit init service.
     *
     * @param mykitCoordinatorService the mykit coordinator service
     */
    @Autowired
    public MykitInitServiceImpl(final MykitCoordinatorService mykitCoordinatorService) {
        this.mykitCoordinatorService = mykitCoordinatorService;
    }

    @Override
    public void initialization(final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.error("mykit-transaction-message have error!")));
        try {
            loadSpiSupport(mykitTransactionMessageConfig);
            mykitCoordinatorService.start(mykitTransactionMessageConfig);
        } catch (Exception ex) {
            LogUtil.error(LOGGER, "Mykit init fail:{}", ex::getMessage);
            //非正常关闭
            System.exit(1);
        }
        new MykitLogo().logo();
    }

    /**
     * load spi support.
     *
     * @param mykitTransactionMessageConfig {@linkplain MykitTransactionMessageConfig}
     */
    private void loadSpiSupport(final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        //spi serialize
        final ObjectSerializer serializer = ExtensionLoader.getExtensionLoader(ObjectSerializer.class)
                .getActivateExtension(mykitTransactionMessageConfig.getSerializer());

        SpringBeanUtils.getInstance().registerBean(ObjectSerializer.class.getName(), serializer);

        //spi repository
        final MykitCoordinatorRepository repository = ExtensionLoader.getExtensionLoader(MykitCoordinatorRepository.class)
                .getActivateExtension(mykitTransactionMessageConfig.getRepositorySupport());

        repository.setSerializer(serializer);
        SpringBeanUtils.getInstance().registerBean(MykitCoordinatorRepository.class.getName(), repository);
    }
}
