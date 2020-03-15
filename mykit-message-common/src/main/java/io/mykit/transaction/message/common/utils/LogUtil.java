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

import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author binghe
 * @version 1.0.0
 * @description LogUtil
 */
public final class LogUtil {
    private static final LogUtil LOG_UTIL = new LogUtil();

    private LogUtil() {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static LogUtil getInstance() {
        return LOG_UTIL;
    }

    /**
     * Debug.
     *
     * @param logger   the logger
     * @param format   the format
     * @param supplier the supplier
     */
    public static void debug(Logger logger, String format, Supplier<Object> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, supplier.get());
        }
    }

    /**
     * Debug.
     *
     * @param logger   the logger
     * @param supplier the supplier
     */
    public static void debug(Logger logger, Supplier<Object> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug(Objects.toString(supplier.get()));
        }
    }

    /**
     * Info.
     *
     * @param logger   the logger
     * @param format   the format
     * @param supplier the supplier
     */
    public static void info(Logger logger, String format, Supplier<Object> supplier) {
        if (logger.isInfoEnabled()) {
            logger.info(format, supplier.get());
        }
    }

    /**
     * Info.
     *
     * @param logger   the logger
     * @param supplier the supplier
     */
    public static void info(Logger logger, Supplier<Object> supplier) {
        if (logger.isInfoEnabled()) {
            logger.info(Objects.toString(supplier.get()));
        }
    }

    /**
     * Error.
     *
     * @param logger   the logger
     * @param format   the format
     * @param supplier the supplier
     */
    public static void error(Logger logger, String format, Supplier<Object> supplier) {
        if (logger.isErrorEnabled()) {
            logger.error(format, supplier.get());
        }
    }

    /**
     * Error.
     *
     * @param logger   the logger
     * @param supplier the supplier
     */
    public static void error(Logger logger, Supplier<Object> supplier) {
        if (logger.isErrorEnabled()) {
            logger.error(Objects.toString(supplier.get()));
        }
    }

    /**
     * Warn.
     *
     * @param logger   the logger
     * @param format   the format
     * @param supplier the supplier
     */
    public static void warn(Logger logger, String format, Supplier<Object> supplier) {
        if (logger.isWarnEnabled()) {
            logger.warn(format, supplier.get());
        }
    }

    /**
     * Warn.
     *
     * @param logger   the logger
     * @param supplier the supplier
     */
    public static void warn(Logger logger, Supplier<Object> supplier) {
        if (logger.isWarnEnabled()) {
            logger.warn(Objects.toString(supplier.get()));
        }
    }
}
