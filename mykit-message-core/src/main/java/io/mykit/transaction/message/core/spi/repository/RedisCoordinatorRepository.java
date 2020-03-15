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
package io.mykit.transaction.message.core.spi.repository;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.bean.adapter.CoordinatorRepositoryAdapter;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.config.MykitTransactionMessageRedisConfig;
import io.mykit.transaction.message.common.constant.CommonConstant;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum;
import io.mykit.transaction.message.common.exception.MykitException;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.jedis.JedisClient;
import io.mykit.transaction.message.common.jedis.JedisClientCluster;
import io.mykit.transaction.message.common.jedis.JedisClientSentinel;
import io.mykit.transaction.message.common.jedis.JedisClientSingle;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;
import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.common.utils.RepositoryConvertUtils;
import io.mykit.transaction.message.common.utils.RepositoryPathUtils;
import io.mykit.transaction.message.core.spi.MykitCoordinatorRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author binghe
 * @version 1.0.0
 * @description RedisCoordinatorRepository
 */
@MykitTransactionMessageSPI("redis")
public class RedisCoordinatorRepository implements MykitCoordinatorRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCoordinatorRepository.class);

    private ObjectSerializer objectSerializer;

    private JedisClient jedisClient;

    private String keyPrefix;

    @Override
    public int create(final MykitTransaction mythTransaction) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, mythTransaction.getTransId());
            jedisClient.set(redisKey, RepositoryConvertUtils.convert(mythTransaction, objectSerializer));
            return CommonConstant.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return CommonConstant.ERROR;
        }
    }

    @Override
    public int remove(final String transId) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, transId);
            return jedisClient.del(redisKey).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonConstant.ERROR;
        }
    }

    @Override
    public int update(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, mythTransaction.getTransId());
            mythTransaction.setVersion(mythTransaction.getVersion() + 1);
            mythTransaction.setLastTime(new Date());
            mythTransaction.setRetriedCount(mythTransaction.getRetriedCount() + 1);
            jedisClient.set(redisKey, RepositoryConvertUtils.convert(mythTransaction, objectSerializer));
            return CommonConstant.SUCCESS;
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
    }

    @Override
    public void updateFailTransaction(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, mythTransaction.getTransId());
            mythTransaction.setLastTime(new Date());
            jedisClient.set(redisKey, RepositoryConvertUtils.convert(mythTransaction, objectSerializer));
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
    }

    @Override
    public void updateParticipant(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, mythTransaction.getTransId());
        byte[] contents = jedisClient.get(redisKey.getBytes());
        try {
            if (contents != null) {
                CoordinatorRepositoryAdapter adapter = objectSerializer.deSerialize(contents, CoordinatorRepositoryAdapter.class);
                adapter.setContents(objectSerializer.serialize(mythTransaction.getMykitTransactionParticipants()));
                jedisClient.set(redisKey, objectSerializer.serialize(adapter));
            }
        } catch (MykitException e) {
            e.printStackTrace();
            throw new MykitRuntimeException(e);
        }
    }

    @Override
    public int updateStatus(final String id, final Integer status) throws MykitRuntimeException {
        final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, id);
        byte[] contents = jedisClient.get(redisKey.getBytes());
        try {
            if (contents != null) {
                CoordinatorRepositoryAdapter adapter = objectSerializer.deSerialize(contents, CoordinatorRepositoryAdapter.class);
                adapter.setStatus(status);
                jedisClient.set(redisKey, objectSerializer.serialize(adapter));
            }
        } catch (MykitException e) {
            e.printStackTrace();
            throw new MykitRuntimeException(e);
        }
        return CommonConstant.SUCCESS;
    }

    @Override
    public MykitTransaction findByTransId(final String transId) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, transId);
            byte[] contents = jedisClient.get(redisKey.getBytes());
            return RepositoryConvertUtils.transformBean(contents, objectSerializer);
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
    }

    @Override
    public List<MykitTransaction> listAllByDelay(final Date date) {
        final List<MykitTransaction> mythTransactionList = listAll();
        return mythTransactionList.stream()
                .filter(mythTransaction -> mythTransaction.getLastTime().compareTo(date) > 0)
                .filter(mythTransaction -> mythTransaction.getStatus() == MykitTransactionMessageStatusEnum.BEGIN.getCode())
                .collect(Collectors.toList());
    }

    private List<MykitTransaction> listAll() {
        try {
            List<MykitTransaction> transactions = Lists.newArrayList();
            Set<byte[]> keys = jedisClient.keys((keyPrefix + "*").getBytes());
            for (final byte[] key : keys) {
                byte[] contents = jedisClient.get(key);
                if (contents != null) {
                    transactions.add(RepositoryConvertUtils.transformBean(contents, objectSerializer));
                }
            }
            return transactions;
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
    }

    @Override
    public void init(final String modelName, final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        keyPrefix = RepositoryPathUtils.buildRedisKeyPrefix(modelName);
        final MykitTransactionMessageRedisConfig mykitTransactionMessageRedisConfig = mykitTransactionMessageConfig.getMykitTransactionMessageRedisConfig();
        try {
            buildJedisPool(mykitTransactionMessageRedisConfig);
        } catch (Exception e) {
            LogUtil.error(LOGGER, "redis init error please check your config ! ex:{}", e::getMessage);
            throw new MykitRuntimeException(e);
        }
    }

    @Override
    public void setSerializer(final ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private void buildJedisPool(final MykitTransactionMessageRedisConfig mykitTransactionMessageRedisConfig) {
        LogUtil.debug(LOGGER, () -> "mykit begin init redis....");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(mykitTransactionMessageRedisConfig.getMaxIdle());
        //最小空闲连接数, 默认0
        config.setMinIdle(mykitTransactionMessageRedisConfig.getMinIdle());
        //最大连接数, 默认8个
        config.setMaxTotal(mykitTransactionMessageRedisConfig.getMaxTotal());
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(mykitTransactionMessageRedisConfig.getMaxWaitMillis());
        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(mykitTransactionMessageRedisConfig.getTestOnBorrow());
        //返回一个jedis实例给连接池时，是否检查连接可用性（ping()）
        config.setTestOnReturn(mykitTransactionMessageRedisConfig.getTestOnReturn());
        //在空闲时检查有效性, 默认false
        config.setTestWhileIdle(mykitTransactionMessageRedisConfig.getTestWhileIdle());
        //逐出连接的最小空闲时间 默认1800000毫秒(30分钟 )
        config.setMinEvictableIdleTimeMillis(mykitTransactionMessageRedisConfig.getMinEvictableIdleTimeMillis());
        //对象空闲多久后逐出, 当空闲时间>该值 ，且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)，默认30m
        config.setSoftMinEvictableIdleTimeMillis(mykitTransactionMessageRedisConfig.getSoftMinEvictableIdleTimeMillis());
        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        config.setTimeBetweenEvictionRunsMillis(mykitTransactionMessageRedisConfig.getTimeBetweenEvictionRunsMillis());
        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        config.setNumTestsPerEvictionRun(mykitTransactionMessageRedisConfig.getNumTestsPerEvictionRun());

        JedisPool jedisPool;
        //如果是集群模式
        if (mykitTransactionMessageRedisConfig.getCluster()) {
            LogUtil.info(LOGGER, () -> "myth build redis cluster ............");
            final String clusterUrl = mykitTransactionMessageRedisConfig.getClusterUrl();
            final Set<HostAndPort> hostAndPorts =
                    Splitter.on(";")
                            .splitToList(clusterUrl)
                            .stream()
                            .map(HostAndPort::parseString).collect(Collectors.toSet());
            JedisCluster jedisCluster = new JedisCluster(hostAndPorts, config);
            jedisClient = new JedisClientCluster(jedisCluster);
        } else if (mykitTransactionMessageRedisConfig.getSentinel()) {
            LogUtil.info(LOGGER, () -> "myth build redis sentinel ............");
            final String sentinelUrl = mykitTransactionMessageRedisConfig.getSentinelUrl();
            final Set<String> hostAndPorts =
                    new HashSet<>(Splitter.on(";")
                            .splitToList(sentinelUrl));

            JedisSentinelPool pool =
                    new JedisSentinelPool(mykitTransactionMessageRedisConfig.getMasterName(), hostAndPorts,
                            config, mykitTransactionMessageRedisConfig.getTimeOut(), mykitTransactionMessageRedisConfig.getPassword());
            jedisClient = new JedisClientSentinel(pool);
        } else {
            if (StringUtils.isNoneBlank(mykitTransactionMessageRedisConfig.getPassword())) {
                jedisPool = new JedisPool(config, mykitTransactionMessageRedisConfig.getHostName(), mykitTransactionMessageRedisConfig.getPort(),
                        mykitTransactionMessageRedisConfig.getTimeOut(),
                        mykitTransactionMessageRedisConfig.getPassword());
            } else {
                jedisPool = new JedisPool(config, mykitTransactionMessageRedisConfig.getHostName(), mykitTransactionMessageRedisConfig.getPort(),
                        mykitTransactionMessageRedisConfig.getTimeOut());
            }
            jedisClient = new JedisClientSingle(jedisPool);
        }
    }
}
