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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author binghe
 * @version 1.0.0
 * @description RepositorySupportEnum
 */
public enum RepositorySupportEnum {
    /**
     * Db compensate cache type enum.
     */
    DB("db"),

    /**
     * File compensate cache type enum.
     */
    FILE("file"),

    /**
     * Redis compensate cache type enum.
     */
    REDIS("redis"),

    /**
     * Mongodb compensate cache type enum.
     */
    MONGODB("mongodb"),

    /**
     * Zookeeper compensate cache type enum.
     */
    ZOOKEEPER("zookeeper");

    private String support;

    RepositorySupportEnum(final String support) {
        this.support = support;
    }

    /**
     * Acquire compensate cache type compensate cache type enum.
     *
     * @param support the compensate cache type
     * @return the compensate cache type enum
     */
    public static RepositorySupportEnum acquire(final String support) {
        Optional<RepositorySupportEnum> repositorySupportEnum =
                Arrays.stream(RepositorySupportEnum.values())
                        .filter(v -> Objects.equals(v.getSupport(), support))
                        .findFirst();
        return repositorySupportEnum.orElse(RepositorySupportEnum.DB);
    }

    /**
     * Gets support.
     *
     * @return the support
     */
    public String getSupport() {
        return support;
    }

    /**
     * Sets support.
     *
     * @param support the support
     */
    public void setSupport(final String support) {
        this.support = support;
    }
}
