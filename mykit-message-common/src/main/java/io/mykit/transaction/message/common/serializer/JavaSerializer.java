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

import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.exception.MykitException;

import java.io.*;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
@MykitTransactionMessageSPI("jdk")
public class JavaSerializer implements ObjectSerializer {
    @Override
    public byte[] serialize(final Object obj) throws MykitException {
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(); ObjectOutput objectOutput = new ObjectOutputStream(arrayOutputStream)) {
            objectOutput.writeObject(obj);
            objectOutput.flush();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new MykitException("java serialize error " + e.getMessage());
        }
    }

    @Override
    public <T> T deSerialize(final byte[] param, final Class<T> clazz) throws MykitException {
        try (ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(param); ObjectInput input = new ObjectInputStream(arrayInputStream)) {
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new MykitException("java deSerialize error " + e.getMessage());
        }
    }
}
