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

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.exception.MykitException;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
@MykitTransactionMessageSPI("protostuff")
public class ProtostuffSerializer implements ObjectSerializer {

    private static final SchemaCache CACHED_SCHEMA = SchemaCache.getInstance();

    private static final Objenesis OBJENESIS = new ObjenesisStd(true);

    private static <T> Schema<T> getSchema(final Class<T> cls) {
        return (Schema<T>) CACHED_SCHEMA.get(cls);
    }

    @Override
    public byte[] serialize(final Object obj) throws MykitException {
        Class cls = obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Schema schema = getSchema(cls);
            ProtostuffIOUtil.writeTo(outputStream, obj, schema, buffer);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new MykitException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deSerialize(final byte[] param, final Class<T> clazz) throws MykitException {
        T object;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(param)) {
            object = OBJENESIS.newInstance(clazz);
            Schema schema = getSchema((Class) clazz);
            ProtostuffIOUtil.mergeFrom(inputStream, object, schema);
            return object;
        } catch (IOException e) {
            throw new MykitException(e.getMessage(), e);
        }
    }
}
