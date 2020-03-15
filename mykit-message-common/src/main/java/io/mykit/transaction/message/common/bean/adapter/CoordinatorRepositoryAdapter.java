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
package io.mykit.transaction.message.common.bean.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author binghe
 * @version 1.0.0
 * @description CoordinatorRepositoryAdapter适配器
 */
@Data
@NoArgsConstructor
public class CoordinatorRepositoryAdapter {
    /**
     * transId.
     */
    private String transId;

    /**
     * status. {@linkplain io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum}
     */
    private int status;

    /**
     * role. {@linkplain io.mykit.transaction.message.common.enums.MykitTransactionMessageRoleEnum}
     */
    private int role;

    /**
     * retriedCount.
     */
    private volatile int retriedCount;

    /**
     * createTime.
     */
    private Date createTime;

    /**
     * lastTime.
     */
    private Date lastTime;

    /**
     * version.
     */
    private Integer version = 1;

    /**
     * pattern.
     */
    private Integer pattern;

    /**
     * contents.
     */
    private byte[] contents;

    /**
     * targetClass.
     */
    private String targetClass;

    /**
     * targetMethod.
     */
    private String targetMethod;

    /**
     * errorMsg.
     */
    private String errorMsg;
}
