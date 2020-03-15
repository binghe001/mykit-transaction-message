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
package io.mykit.transaction.message.common.utils.extension;

import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.exception.MykitException;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * @author binghe
 * @version 1.0.0
 * @description 扩展类加载器
 */
public class ExtensionLoader <T> {
    private Class<T> type;

    private ExtensionLoader(final Class<T> type) {
        this.type = type;
    }

    private static <T> boolean withExtensionAnnotation(final Class<T> type) {
        return type.isAnnotationPresent(MykitTransactionMessageSPI.class);
    }

    /**
     * Gets extension loader.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the extension loader
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> type) {
        if (type == null) {
            throw new MykitException("type == null");
        }
        if (!type.isInterface()) {
            throw new MykitException("Extension type(" + type + ") not interface!");
        }
        if (!withExtensionAnnotation(type)) {
            throw new MykitException("type" + type.getName() + "not exist");
        }
        return new ExtensionLoader<>(type);
    }

    /**
     * Gets activate extension.
     *
     * @param value the value
     * @return the activate extension
     */
    public T getActivateExtension(final String value) {
        ServiceLoader<T> loader = ServiceBootstrap.loadAll(type);
        return StreamSupport.stream(loader.spliterator(), false)
                .filter(e -> Objects.equals(e.getClass()
                        .getAnnotation(MykitTransactionMessageSPI.class).value(), value))
                .findFirst().orElseThrow(() -> new MykitException("Please check your configuration"));
    }
}
