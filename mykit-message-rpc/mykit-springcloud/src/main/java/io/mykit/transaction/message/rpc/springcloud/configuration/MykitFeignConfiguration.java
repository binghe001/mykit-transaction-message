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
package io.mykit.transaction.message.rpc.springcloud.configuration;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import feign.RequestInterceptor;
import io.mykit.transaction.message.rpc.springcloud.fegin.MykitFeignBeanPostProcessor;
import io.mykit.transaction.message.rpc.springcloud.fegin.MykitFeignInterceptor;
import io.mykit.transaction.message.rpc.springcloud.hystrix.MykitHystrixConcurrencyStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitFeignConfiguration
 */
@Configuration
public class MykitFeignConfiguration {

    /**
     * Request interceptor request interceptor.
     *
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new MykitFeignInterceptor();
    }

    /**
     * MythFeign post processor myth feign bean post processor.
     *
     * @return the myth feign bean post processor
     */
    @Bean
    public MykitFeignBeanPostProcessor mythFeignPostProcessor() {
        return new MykitFeignBeanPostProcessor();
    }

    /**
     * Hystrix concurrency strategy hystrix concurrency strategy.
     *
     * @return the hystrix concurrency strategy
     */
    @Bean
    @ConditionalOnProperty(name = "feign.hystrix.enabled")
    public HystrixConcurrencyStrategy hystrixConcurrencyStrategy() {
        return new MykitHystrixConcurrencyStrategy();
    }
}
