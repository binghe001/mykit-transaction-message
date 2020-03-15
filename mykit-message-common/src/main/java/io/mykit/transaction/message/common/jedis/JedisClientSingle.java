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
package io.mykit.transaction.message.common.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * @author binghe
 * @version 1.0.0
 * @description JedisClientSingle
 */
public class JedisClientSingle implements JedisClient {
    private JedisPool jedisPool;

    /**
     * Instantiates a new Jedis client single.
     *
     * @param jedisPool the jedis pool
     */
    public JedisClientSingle(final JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public String set(final String key, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    @Override
    public String set(final String key, final byte[] value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key.getBytes(), value);
        }
    }

    @Override
    public Long del(final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys);
        }
    }

    @Override
    public String get(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public byte[] get(final byte[] key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public Set<byte[]> keys(final byte[] pattern) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(pattern);
        }
    }

    @Override
    public Set<String> keys(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(key);
        }
    }

    @Override
    public Long hset(final String key, final String item, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, item, value);
        }
    }

    @Override
    public String hget(final String key, final String item) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, item);
        }
    }

    @Override
    public Long hdel(final String key, final String item) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hdel(key, item);
        }
    }

    @Override
    public Long incr(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        }
    }

    @Override
    public Long decr(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decr(key);
        }
    }

    @Override
    public Long expire(final String key, final int second) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, second);
        }
    }

    @Override
    public Set<String> zrange(final String key, final long start, final long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(key, start, end);
        }
    }
}
