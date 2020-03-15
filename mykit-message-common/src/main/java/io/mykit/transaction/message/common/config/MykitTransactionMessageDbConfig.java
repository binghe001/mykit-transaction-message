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
package io.mykit.transaction.message.common.config;

import lombok.Data;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitTransactionMessageDbConfig
 */
@Data
public class MykitTransactionMessageDbConfig {

    private String driverClassName = "com.mysql.jdbc.Driver";

    private String url;

    private String username;

    private String password;

    private int initialSize = 10;

    private int maxActive = 100;

    private int minIdle = 20;

    private int maxWait = 60000;

    private int timeBetweenEvictionRunsMillis = 60000;

    /**
     * To configure a connection in the pool minimum survival time, unit is milliseconds.
     */
    private int minEvictableIdleTimeMillis = 300000;

    private String validationQuery = " SELECT 1 ";

    /**
     * Apply for connection to perform validation Query test connection is valid, do this configuration will degrade performance.
     */
    private Boolean testOnBorrow = false;

    /**
     * Return connection to perform validation Query test connection is valid, do this configuration will degrade performance.
     */
    private Boolean testOnReturn = false;

    /**
     * Recommendations to true, do not affect performance,
     * and ensure safety. Application connection testing,
     * if free time is greater than the time Between Eviction Runs Millis,
     * perform the validation Query test connection is valid..
     */
    private Boolean testWhileIdle = true;

    /**
     * Whether the cache prepared Statement,
     * namely PSCache. PSCache cursor database to support huge performance improvements,
     * such as oracle. Under the mysql suggested that shut down.
     */
    private Boolean poolPreparedStatements = false;

    /**
     * To enable the PSCache, you must configure greater than zero,
     * when greater than 0,
     * the pool Prepared Statements trigger automatically modified to true.
     * In the Druid, not Oracle PSCache under too much memory problems,
     * can put this value configuration, such as 100.
     */
    private int maxPoolPreparedStatementPerConnectionSize = 100;
}
