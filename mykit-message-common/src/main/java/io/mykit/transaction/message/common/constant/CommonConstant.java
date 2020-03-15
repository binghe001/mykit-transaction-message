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
package io.mykit.transaction.message.common.constant;

/**
 * @author binghe
 * @version 1.0.0
 * @description CommonConstant接口
 */
public interface CommonConstant {
    /**
     * The constant LINE_SEPARATOR.
     */
    String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * The constant DB_MYSQL.
     */
    String DB_MYSQL = "mysql";

    /**
     * The constant DB_SQLSERVER.
     */
    String DB_SQLSERVER = "sqlserver";

    /**
     * The constant DB_ORACLE.
     */
    String DB_ORACLE = "oracle";

    /**
     * The constant PATH_SUFFIX.
     */
    String PATH_SUFFIX = "/mykit_transaction_message";

    /**
     * The constant DB_SUFFIX.
     */
    String DB_SUFFIX = "mykit_transaction_message_";

    /**
     * The constant RECOVER_REDIS_KEY_PRE.
     */
    String RECOVER_REDIS_KEY_PRE = "mykit:transaction:message:%s";

    /**
     * The constant MYKIT_TRANSACTION_MESSAGE_CONTEXT.
     */
    String MYKIT_TRANSACTION_MESSAGE_CONTEXT = "MYKIT_TRANSACTION_MESSAGE_CONTEXT";

    /**
     * The constant TOPIC_TAG_SEPARATOR.
     */
    String TOPIC_TAG_SEPARATOR = ",";

    /**
     * The constant SUCCESS.
     */
    int SUCCESS = 1;

    /**
     * The constant ERROR.
     */
    int ERROR = 0;
}
