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
package io.mykit.transaction.message.common.bean.mq;

import io.mykit.transaction.message.common.bean.entity.MykitTransactionInvocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author binghe
 * @version 1.0.0
 * @description 消息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    private String transId;

    private MykitTransactionInvocation mykitTransactionMessageInvocation;
}
