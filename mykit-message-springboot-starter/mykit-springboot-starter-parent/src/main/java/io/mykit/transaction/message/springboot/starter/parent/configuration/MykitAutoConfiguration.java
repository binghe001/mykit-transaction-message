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
package io.mykit.transaction.message.springboot.starter.parent.configuration;

import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.core.bootstrap.MykitTransactionBootstrap;
import io.mykit.transaction.message.core.service.MykitInitService;
import io.mykit.transaction.message.springboot.starter.parent.config.MykitConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitAutoConfiguration
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties
@ComponentScan(basePackages = {"io.mykit.transaction"})
public class MykitAutoConfiguration {
    private final MykitConfigProperties mykitConfigProperties;

    /**
     * Instantiates a new Mykit auto configuration.
     *
     * @param mykitConfigProperties the mykit config properties
     */
    @Autowired(required = false)
    public MykitAutoConfiguration(final MykitConfigProperties mykitConfigProperties) {
        this.mykitConfigProperties = mykitConfigProperties;
    }

    /**
     * init MythTransactionBootstrap.
     *
     * @param mykitInitService {@linkplain MykitInitService}
     * @return MythTransactionBootstrap mykit transaction bootstrap
     */
    @Bean
    public MykitTransactionBootstrap tccTransactionBootstrap(final MykitInitService mykitInitService) {
        final MykitTransactionBootstrap bootstrap = new MykitTransactionBootstrap(mykitInitService);
        bootstrap.builder(builder());
        return bootstrap;
    }

    /**
     * init bean of  MykitConfig.
     *
     * @return {@linkplain MykitTransactionMessageConfig}
     */
    @Bean
    public MykitTransactionMessageConfig mykitTransactionMessageConfig() {
        return builder().build();
    }

    private MykitTransactionMessageConfig.Builder builder() {
        return MykitTransactionBootstrap.create()
                .setSerializer(mykitConfigProperties.getSerializer())
                .setRepositorySuffix(mykitConfigProperties.getRepositorySuffix())
                .setRepositorySupport(mykitConfigProperties.getRepositorySupport())
                .setNeedRecover(mykitConfigProperties.getNeedRecover())
                .setBufferSize(mykitConfigProperties.getBufferSize())
                .setConsumerThreads(mykitConfigProperties.getConsumerThreads())
                .setScheduledThreadMax(mykitConfigProperties.getScheduledThreadMax())
                .setScheduledDelay(mykitConfigProperties.getScheduledDelay())
                .setRetryMax(mykitConfigProperties.getRetryMax())
                .setRecoverDelayTime(mykitConfigProperties.getRecoverDelayTime())
                .setMykitTransactionMessageDbConfig(mykitConfigProperties.getMykitTransactionMessageDbConfig())
                .setMykitTransactionMessageFileConfig(mykitConfigProperties.getMykitTransactionMessageFileConfig())
                .setMykitTransactionMessageMongoConfig(mykitConfigProperties.getMykitTransactionMessageMongoConfig())
                .setMykitTransactionMessageRedisConfig(mykitConfigProperties.getMykitTransactionMessageRedisConfig())
                .setMykitTransactionMessageZookeeperConfig(mykitConfigProperties.getMykitTransactionMessageZookeeperConfig());
    }
}
