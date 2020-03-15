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
package io.mykit.transaction.message.core.helper;

import io.mykit.transaction.message.common.utils.AssertUtils;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author binghe
 * @version 1.0.0
 * @description SpringBeanUtils
 */
public final class SpringBeanUtils {

    private static final SpringBeanUtils INSTANCE = new SpringBeanUtils();

    private ConfigurableApplicationContext cfgContext;

    private SpringBeanUtils() {
        if (INSTANCE != null) {
            throw new Error("error");
        }
    }

    /**
     * get SpringBeanUtils.
     *
     * @return SpringBeanUtils
     */
    public static SpringBeanUtils getInstance() {
        return INSTANCE;
    }

    /**
     * acquire spring bean.
     *
     * @param type type
     * @param <T>  class
     * @return bean
     */
    public <T> T getBean(final Class<T> type) {
        AssertUtils.notNull(type);
        return cfgContext.getBean(type);
    }

    /**
     * register bean in spring ioc.
     *
     * @param beanName bean name
     * @param obj      bean
     */
    public void registerBean(final String beanName, final Object obj) {
        AssertUtils.notNull(beanName);
        AssertUtils.notNull(obj);
        cfgContext.getBeanFactory().registerSingleton(beanName, obj);
    }

    /**
     * set application context.
     *
     * @param cfgContext application context
     */
    public void setCfgContext(final ConfigurableApplicationContext cfgContext) {
        this.cfgContext = cfgContext;
    }
}
