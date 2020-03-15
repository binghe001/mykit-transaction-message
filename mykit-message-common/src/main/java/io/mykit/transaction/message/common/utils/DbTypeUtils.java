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
package io.mykit.transaction.message.common.utils;

import io.mykit.transaction.message.common.constant.CommonConstant;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
public class DbTypeUtils {

    private static final String DB_TYPE_MYSQL = "mysql";
    private static final String DB_TYPE_SQLSERVER = "sqlserver";
    private static final String DB_TYPE_ORACLE = "oracle";
    /**
     * Build by driver class name string.
     *
     * @param driverClassName the driver class name
     * @return the string
     */
    public static String buildByDriverClassName(final String driverClassName) {
        String dbType = DB_TYPE_MYSQL;
        if (driverClassName.contains(CommonConstant.DB_MYSQL)) {
            dbType = DB_TYPE_MYSQL;
        } else if (driverClassName.contains(CommonConstant.DB_SQLSERVER)) {
            dbType = DB_TYPE_SQLSERVER;
        } else if (driverClassName.contains(CommonConstant.DB_ORACLE)) {
            dbType = DB_TYPE_ORACLE;
        }
        return dbType;
    }
}
