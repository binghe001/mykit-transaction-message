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
package io.mykit.transaction.message.common.serializer;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author binghe
 * @version 1.0.0
 * @description SchemaCache
 */
public class SchemaCache {

    private Cache<Class<?>, Schema<?>> cache = CacheBuilder.newBuilder()
            .maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS).build();

    protected static SchemaCache getInstance() {
        return SchemaCacheHolder.cache;
    }

    private Schema<?> get(final Class<?> cls, final Cache<Class<?>, Schema<?>> cache) {
        try {
            return cache.get(cls, () -> RuntimeSchema.createFrom(cls));
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * acquire Schema with class.
     *
     * @param clazz Class
     * @return  Schema
     */
    public Schema<?> get(final Class<?> clazz) {
        return get(clazz, cache);
    }

    private static class SchemaCacheHolder {
        private static SchemaCache cache = new SchemaCache();
    }
}
