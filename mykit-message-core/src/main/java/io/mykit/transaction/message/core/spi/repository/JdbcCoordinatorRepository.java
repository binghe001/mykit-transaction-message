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

import com.alibaba.druid.pool.DruidDataSource;
import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionParticipant;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.config.MykitTransactionMessageDbConfig;
import io.mykit.transaction.message.common.constant.CommonConstant;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum;
import io.mykit.transaction.message.common.exception.MykitException;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;
import io.mykit.transaction.message.common.utils.RepositoryPathUtils;
import io.mykit.transaction.message.core.helper.SqlHelper;
import io.mykit.transaction.message.core.spi.MykitCoordinatorRepository;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author binghe
 * @version 1.0.0
 * @description JdbcCoordinatorRepository
 */
@MykitTransactionMessageSPI("db")
public class JdbcCoordinatorRepository implements MykitCoordinatorRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcCoordinatorRepository.class);

    private DruidDataSource dataSource;

    private String tableName;

    private ObjectSerializer serializer;

    @Override
    public void setSerializer(final ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public int create(final MykitTransaction mythTransaction) {
        StringBuilder sql = new StringBuilder()
                .append("insert into ")
                .append(tableName)
                .append("(trans_id,target_class,target_method,retried_count,create_time,last_time,version,status,invocation,role,error_msg)")
                .append(" values(?,?,?,?,?,?,?,?,?,?,?)");
        try {
            final byte[] serialize = serializer.serialize(mythTransaction.getMykitTransactionParticipants());
            return executeUpdate(sql.toString(),
                    mythTransaction.getTransId(),
                    mythTransaction.getTargetClass(),
                    mythTransaction.getTargetMethod(),
                    mythTransaction.getRetriedCount(),
                    mythTransaction.getCreateTime(),
                    mythTransaction.getLastTime(),
                    mythTransaction.getVersion(),
                    mythTransaction.getStatus(),
                    serialize,
                    mythTransaction.getRole(),
                    mythTransaction.getErrorMsg());
        } catch (MykitException e) {
            e.printStackTrace();
            return CommonConstant.ERROR;
        }
    }

    @Override
    public int remove(final String transId) {
        String sql = "delete from " + tableName + " where trans_id = ? ";
        return executeUpdate(sql, transId);
    }

    @Override
    public int update(final MykitTransaction mykitTransaction) throws MykitRuntimeException {
        final Integer currentVersion = mykitTransaction.getVersion();
        mykitTransaction.setLastTime(new Date());
        mykitTransaction.setVersion(mykitTransaction.getVersion() + 1);
        String sql = "update " + tableName + " set last_time = ?,version =?,retried_count =?,invocation=?,status=?  where trans_id = ? and version=? ";
        try {
            final byte[] serialize = serializer.serialize(mykitTransaction.getMykitTransactionParticipants());
            return executeUpdate(sql,
                    mykitTransaction.getLastTime(),
                    mykitTransaction.getVersion(),
                    mykitTransaction.getRetriedCount(),
                    serialize,
                    mykitTransaction.getStatus(),
                    mykitTransaction.getTransId(),
                    currentVersion);
        } catch (MykitException e) {
            throw new MykitRuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateFailTransaction(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        String sql = "update " + tableName + " set  status=? ,error_msg=? ,retried_count =?,last_time = ?   where trans_id = ?  ";
        mythTransaction.setLastTime(new Date());
        executeUpdate(sql, mythTransaction.getStatus(),
                mythTransaction.getErrorMsg(),
                mythTransaction.getRetriedCount(),
                mythTransaction.getLastTime(),
                mythTransaction.getTransId());
    }

    @Override
    public void updateParticipant(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        String sql = "update " + tableName + " set invocation=?  where trans_id = ?  ";
        try {
            final byte[] serialize = serializer.serialize(mythTransaction.getMykitTransactionParticipants());
            executeUpdate(sql, serialize, mythTransaction.getTransId());
        } catch (MykitException e) {
            throw new MykitRuntimeException(e.getMessage());
        }
    }

    @Override
    public int updateStatus(final String id, final Integer status) throws MykitRuntimeException {
        String sql = "update " + tableName + " set status=?  where trans_id = ?  ";
        return executeUpdate(sql, status, id);
    }

    @Override
    public MykitTransaction findByTransId(final String transId) {
        String selectSql = "select * from " + tableName + " where trans_id=?";
        List<Map<String, Object>> list = executeQuery(selectSql, transId);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().filter(Objects::nonNull)
                    .map(this::buildByResultMap).collect(Collectors.toList()).get(0);
        }
        return null;
    }

    @Override
    public List<MykitTransaction> listAllByDelay(final Date date) {
        String sb = "select * from " + tableName + " where last_time < ?  and status = " + MykitTransactionMessageStatusEnum.BEGIN.getCode();
        List<Map<String, Object>> list = executeQuery(sb, date);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(this::buildByResultMap)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private MykitTransaction buildByResultMap(final Map<String, Object> map) {
        MykitTransaction mythTransaction = new MykitTransaction();
        mythTransaction.setTransId((String) map.get("trans_id"));
        mythTransaction.setRetriedCount((Integer) map.get("retried_count"));
        mythTransaction.setCreateTime((Date) map.get("create_time"));
        mythTransaction.setLastTime((Date) map.get("last_time"));
        mythTransaction.setVersion((Integer) map.get("version"));
        mythTransaction.setStatus((Integer) map.get("status"));
        mythTransaction.setRole((Integer) map.get("role"));
        byte[] bytes = (byte[]) map.get("invocation");
        try {
            final List<MykitTransactionParticipant> participants = serializer.deSerialize(bytes, CopyOnWriteArrayList.class);
            mythTransaction.setMykitTransactionParticipants(participants);
        } catch (MykitException e) {
            e.printStackTrace();
        }
        return mythTransaction;
    }

    @Override
    public void init(final String modelName, final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        dataSource = new DruidDataSource();
        final MykitTransactionMessageDbConfig tccDbConfig = mykitTransactionMessageConfig.getMykitTransactionMessageDbConfig();
        dataSource.setUrl(tccDbConfig.getUrl());
        dataSource.setDriverClassName(tccDbConfig.getDriverClassName());
        dataSource.setUsername(tccDbConfig.getUsername());
        dataSource.setPassword(tccDbConfig.getPassword());
        dataSource.setInitialSize(tccDbConfig.getInitialSize());
        dataSource.setMaxActive(tccDbConfig.getMaxActive());
        dataSource.setMinIdle(tccDbConfig.getMinIdle());
        dataSource.setMaxWait(tccDbConfig.getMaxWait());
        dataSource.setValidationQuery(tccDbConfig.getValidationQuery());
        dataSource.setTestOnBorrow(tccDbConfig.getTestOnBorrow());
        dataSource.setTestOnReturn(tccDbConfig.getTestOnReturn());
        dataSource.setTestWhileIdle(tccDbConfig.getTestWhileIdle());
        dataSource.setPoolPreparedStatements(tccDbConfig.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(tccDbConfig.getMaxPoolPreparedStatementPerConnectionSize());
        this.tableName = RepositoryPathUtils.buildDbTableName(modelName);
        executeUpdate(SqlHelper.buildCreateTableSql(tccDbConfig.getDriverClassName(), tableName));
    }

    private int executeUpdate(final String sql, final Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("executeUpdate-> " + e.getMessage());
        }
        return 0;
    }

    private List<Map<String, Object>> executeQuery(final String sql, final Object... params) {
        List<Map<String, Object>> list = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> rowData = new HashMap<>(16);
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.put(md.getColumnName(i), rs.getObject(i));
                    }
                    list.add(rowData);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("executeQuery-> " + e.getMessage());
        }
        return list;
    }
}
