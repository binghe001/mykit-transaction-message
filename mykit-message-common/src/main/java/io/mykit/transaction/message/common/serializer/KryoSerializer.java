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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.exception.MykitException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author binghe
 * @version 1.0.0
 * @description KryoSerializer
 */
@MykitTransactionMessageSPI("kryo")
public class KryoSerializer implements ObjectSerializer {
    @Override
    public byte[] serialize(final Object obj) throws MykitException {
        byte[] bytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); Output output = new Output(outputStream)) {
            //获取kryo对象
            Kryo kryo = new Kryo();
            kryo.writeObject(output, obj);
            bytes = output.toBytes();
            output.flush();
        } catch (IOException ex) {
            throw new MykitException("kryo serialize error" + ex.getMessage());
        }
        return bytes;
    }

    @Override
    public <T> T deSerialize(final byte[] param, final Class<T> clazz) throws MykitException {
        T object;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(param)) {
            Kryo kryo = new Kryo();
            Input input = new Input(inputStream);
            object = kryo.readObject(input, clazz);
            input.close();
        } catch (IOException e) {
            throw new MykitException("kryo deSerialize error" + e.getMessage());
        }
        return object;
    }
}
