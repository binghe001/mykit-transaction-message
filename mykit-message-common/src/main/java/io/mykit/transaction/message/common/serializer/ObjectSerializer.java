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

/**
 * @author binghe
 * @version 1.0.0
 * @description ObjectSerializer
 */
@MykitTransactionMessageSPI
public interface ObjectSerializer {
    /**
     * Serialize byte [ ].
     *
     * @param obj the obj
     * @return the byte [ ]
     * @throws MykitException the myth exception
     */
    byte[] serialize(Object obj) throws MykitException;

    /**
     * De serialize t.
     *
     * @param <T>   the type parameter
     * @param param the param
     * @param clazz the clazz
     * @return the t
     * @throws MykitException the myth exception
     */
    <T> T deSerialize(byte[] param, Class<T> clazz) throws MykitException;
}
