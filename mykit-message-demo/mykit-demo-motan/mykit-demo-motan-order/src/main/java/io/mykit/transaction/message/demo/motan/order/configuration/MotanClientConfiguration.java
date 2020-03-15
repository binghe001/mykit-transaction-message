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
package io.mykit.transaction.message.demo.motan.order.configuration;

import com.weibo.api.motan.config.springsupport.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
@Configuration
public class MotanClientConfiguration implements EnvironmentAware {
    @Resource
    private Environment environment;

    /**
     * Motan annotation bean annotation bean.
     *
     * @return the annotation bean
     */
    @Bean
    public AnnotationBean motanAnnotationBean() {
        AnnotationBean motanAnnotationBean = new AnnotationBean();
        motanAnnotationBean.setPackage("com.github.myth.demo.motan.order.service");
        return motanAnnotationBean;
    }


    /**
     * Registry config registry config bean.
     *
     * @return the registry config bean
     */
    @Bean(name = "registry")
    public RegistryConfigBean registryConfig() {
        RegistryConfigBean config = new RegistryConfigBean();
        config.setRegProtocol("zookeeper");
        config.setAddress(environment.getProperty("spring.motan.zookeeper"));
        config.setConnectTimeout(300000);
        return config;
    }

    /**
     * Protocol config protocol config bean.
     *
     * @return the protocol config bean
     */
    @Bean(name = "motan")
    public ProtocolConfigBean protocolConfig() {
        ProtocolConfigBean config = new ProtocolConfigBean();
        config.setDefault(true);
        config.setName("motan");
        config.setMaxContentLength(5048575);
        return config;
    }

    /**
     * Basic referer config bean basic referer config bean.
     *
     * @return the basic referer config bean
     */
    @Bean(name = "basicRefererConfig")
    public BasicRefererConfigBean basicRefererConfigBean() {
        BasicRefererConfigBean config = new BasicRefererConfigBean();
        config.setProtocol("motan");
        config.setRegistry("registry");
        config.setRequestTimeout(500000);
        config.setRegistry("registry");
        config.setCheck(false);
        config.setAccessLog(true);
        config.setRetries(2);
        config.setThrowException(true);
        return config;
    }

    /**
     * Base service config basic service config bean.
     *
     * @return the basic service config bean
     */
    @Bean
    public BasicServiceConfigBean baseServiceConfig() {
        BasicServiceConfigBean config = new BasicServiceConfigBean();
        config.setExport("motan:8001");
        config.setRegistry("registry");
        config.setAccessLog(false);
        config.setShareChannel(true);
        config.setRequestTimeout(500000);
        config.setUsegz(true);
        config.setCheck(false);
        config.setModule("order_service");
        config.setApplication("order_service");
        return config;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
