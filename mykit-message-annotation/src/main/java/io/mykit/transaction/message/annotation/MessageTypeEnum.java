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
package io.mykit.transaction.message.annotation;

/**
 * @author binghe
 * @version 1.0.0
 * @description 消息类型枚举
 */
@SuppressWarnings("ALL")
public enum MessageTypeEnum {
    /**
     * P 2 p message type enum.
     */
    P2P(1, "点对点模式"),

    /**
     * Topic message type enum.
     */
    TOPIC(2, "TOPIC模式");

    private final Integer code;

    private final String desc;

    MessageTypeEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public Integer getCode() {
        return code;
    }


    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }
}
