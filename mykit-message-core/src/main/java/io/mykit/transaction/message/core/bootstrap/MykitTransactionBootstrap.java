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
package io.mykit.transaction.message.core.bootstrap;

import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.core.helper.SpringBeanUtils;
import io.mykit.transaction.message.core.service.MykitInitService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitTransactionBootstrap
 */
public class MykitTransactionBootstrap extends MykitTransactionMessageConfig implements ApplicationContextAware {

    private final MykitInitService mykitInitService;

    @Autowired
    public MykitTransactionBootstrap(final MykitInitService mykitInitService) {
        this.mykitInitService = mykitInitService;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.getInstance().setCfgContext((ConfigurableApplicationContext) applicationContext);
        start(this);
    }

    private void start(final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        mykitInitService.initialization(mykitTransactionMessageConfig);
    }
}
