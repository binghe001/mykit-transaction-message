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
package io.mykit.transaction.message.rpc.dubbo.service;

import com.alibaba.dubbo.config.ApplicationConfig;
import io.mykit.transaction.message.core.service.MykitApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author binghe
 * @version 1.0.0
 * @description DubboMykitApplicationServiceImpl
 */
@Service("applicationService")
public class DubboMykitApplicationServiceImpl implements MykitApplicationService {

    /**
     * dubbo ApplicationConfig.
     */
    private final ApplicationConfig applicationConfig;

    /**
     * Instantiates a new Dubbo mykit application service.
     *
     * @param applicationConfig the application config
     */
    @Autowired(required = false)
    public DubboMykitApplicationServiceImpl(final ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public String acquireName() {
        return Optional.ofNullable(applicationConfig).orElse(new ApplicationConfig("mykit-dubbo")).getName();

    }
}
