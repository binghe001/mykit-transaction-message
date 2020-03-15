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
package io.mykit.transaction.message.rpc.springcloud.service;

import io.mykit.transaction.message.core.service.MykitApplicationService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author binghe
 * @version 1.0.0
 * @description SpringCloudMykitApplicationServiceImpl
 */
@Service("applicationService")
public class SpringCloudMykitApplicationServiceImpl implements MykitApplicationService {

    private static final String DEFAULT_APPLICATION_NAME = "mykitSpringCloud";

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public String acquireName() {
        return Optional.ofNullable(appName).orElse(buildDefaultApplicationName());
    }

    private String buildDefaultApplicationName() {
        return DEFAULT_APPLICATION_NAME + RandomUtils.nextInt(1, 10);
    }
}
