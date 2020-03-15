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

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.exception.MykitException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author binghe
 * @version 1.0.0
 * @description HessianSerializer
 */
@MykitTransactionMessageSPI("hessian")
public class HessianSerializer implements ObjectSerializer {
    @Override
    public byte[] serialize(final Object obj) throws MykitException {
        Hessian2Output hos;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            hos = new Hessian2Output(bos);
            hos.writeObject(obj);
            hos.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            throw new MykitException("Hessian serialize error " + ex.getMessage());
        }
    }

    @Override
    public <T> T deSerialize(final byte[] param, final Class<T> clazz) throws MykitException {
        ByteArrayInputStream bios;
        try {
            bios = new ByteArrayInputStream(param);
            Hessian2Input his = new Hessian2Input(bios);
            return (T) his.readObject();
        } catch (IOException e) {
            throw new MykitException("Hessian deSerialize error " + e.getMessage());
        }
    }
}
