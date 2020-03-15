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
package io.mykit.transaction.message.common.enums;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitTransactionMessageStatusEnum
 */
public enum  MykitTransactionMessageStatusEnum {
    /**
     * Rollback transaction status enum.
     */
    ROLLBACK(0, "回滚"),

    /**
     * Commit transaction status enum.
     */
    COMMIT(1, "已经提交"),

    /**
     * Begin transaction status enum.
     */
    BEGIN(2, "开始"),

    /**
     * Running transaction status enum.
     */
    SEND_MSG(3, "可以发送消息"),

    /**
     * Failure transaction status enum.
     */
    FAILURE(4, "失败"),

    /**
     * Complete transaction status enum.
     */
    PRE_COMMIT(5, "预提交"),

    /**
     * Lock transaction status enum.
     */
    LOCK(6, "锁定");

    private int code;

    private String desc;

    MykitTransactionMessageStatusEnum(final int code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(final int code) {
        this.code = code;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(final String desc) {
        this.desc = desc;
    }
}
