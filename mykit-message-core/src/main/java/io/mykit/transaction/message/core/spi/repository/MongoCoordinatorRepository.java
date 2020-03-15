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
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.bean.adapter.MongoAdapter;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.bean.entity.MykitTransactionParticipant;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.config.MykitTransactionMessageMongoConfig;
import io.mykit.transaction.message.common.constant.CommonConstant;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum;
import io.mykit.transaction.message.common.exception.MykitException;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;
import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.common.utils.RepositoryPathUtils;
import io.mykit.transaction.message.core.spi.MykitCoordinatorRepository;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author binghe
 * @version 1.0.0
 * @description MongoCoordinatorRepository
 */
@MykitTransactionMessageSPI("mongodb")
public class MongoCoordinatorRepository implements MykitCoordinatorRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoCoordinatorRepository.class);

    private static final String ERROR = "mongo update exception!";

    private ObjectSerializer objectSerializer;

    private MongoTemplate template;

    private String collectionName;

    @Override
    public int create(final MykitTransaction mykitTransaction) {
        try {
            MongoAdapter mongoBean = new MongoAdapter();
            mongoBean.setTransId(mykitTransaction.getTransId());
            mongoBean.setCreateTime(mykitTransaction.getCreateTime());
            mongoBean.setLastTime(mykitTransaction.getLastTime());
            mongoBean.setRetriedCount(mykitTransaction.getRetriedCount());
            mongoBean.setStatus(mykitTransaction.getStatus());
            mongoBean.setRole(mykitTransaction.getRole());
            mongoBean.setTargetClass(mykitTransaction.getTargetClass());
            mongoBean.setTargetMethod(mykitTransaction.getTargetMethod());
            final byte[] cache = objectSerializer.serialize(mykitTransaction.getMykitTransactionParticipants());
            mongoBean.setContents(cache);
            mongoBean.setErrorMsg(mykitTransaction.getErrorMsg());
            template.save(mongoBean, collectionName);
            return CommonConstant.SUCCESS;
        } catch (MykitException e) {
            e.printStackTrace();
            return CommonConstant.ERROR;
        }
    }

    @Override
    public int remove(final String transId) {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(transId));
        template.remove(query, collectionName);
        return CommonConstant.SUCCESS;
    }

    @Override
    public int update(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(mythTransaction.getTransId()));
        Update update = new Update();
        update.set("lastTime", new Date());
        update.set("retriedCount", mythTransaction.getRetriedCount() + 1);
        update.set("version", mythTransaction.getVersion() + 1);
        try {
            if (CollectionUtils.isNotEmpty(mythTransaction.getMykitTransactionParticipants())) {
                update.set("contents", objectSerializer.serialize(mythTransaction.getMykitTransactionParticipants()));
            }
        } catch (MykitException e) {
            e.printStackTrace();
        }
        final WriteResult writeResult = template.updateFirst(query, update, MongoAdapter.class, collectionName);
        if (writeResult.getN() <= 0) {
            throw new MykitRuntimeException(ERROR);
        }
        return CommonConstant.SUCCESS;
    }

    @Override
    public void updateFailTransaction(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(mythTransaction.getTransId()));
        Update update = new Update();
        update.set("status", mythTransaction.getStatus());
        update.set("errorMsg", mythTransaction.getErrorMsg());
        update.set("lastTime", new Date());
        update.set("retriedCount", mythTransaction.getRetriedCount());
        final WriteResult writeResult = template.updateFirst(query, update, MongoAdapter.class, collectionName);
        if (writeResult.getN() <= 0) {
            throw new MykitRuntimeException(ERROR);
        }
    }

    @Override
    public void updateParticipant(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(mythTransaction.getTransId()));
        Update update = new Update();
        try {
            update.set("contents", objectSerializer.serialize(mythTransaction.getMykitTransactionParticipants()));
        } catch (MykitRuntimeException e) {
            e.printStackTrace();
        }
        final WriteResult writeResult = template.updateFirst(query, update, MongoAdapter.class, collectionName);
        if (writeResult.getN() <= 0) {
            throw new MykitRuntimeException(ERROR);
        }
    }

    @Override
    public int updateStatus(final String id, final Integer status) throws MykitRuntimeException {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(id));
        Update update = new Update();
        update.set("status", status);
        final WriteResult writeResult = template.updateFirst(query, update, MongoAdapter.class, collectionName);
        if (writeResult.getN() <= 0) {
            throw new MykitRuntimeException(ERROR);
        }
        return CommonConstant.SUCCESS;
    }

    @Override
    public MykitTransaction findByTransId(final String transId) {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(transId));
        MongoAdapter cache = template.findOne(query, MongoAdapter.class, collectionName);
        return buildByCache(cache);

    }

    @Override
    public List<MykitTransaction> listAllByDelay(final Date date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("lastTime").lt(date))
                .addCriteria(Criteria.where("status").is(MykitTransactionMessageStatusEnum.BEGIN.getCode()));
        final List<MongoAdapter> mongoBeans = template.find(query, MongoAdapter.class, collectionName);
        if (CollectionUtils.isNotEmpty(mongoBeans)) {
            return mongoBeans.stream().map(this::buildByCache).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void init(final String modelName, final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        collectionName = RepositoryPathUtils.buildMongoTableName(modelName);
        final MykitTransactionMessageMongoConfig tccMongoConfig = mykitTransactionMessageConfig.getMykitTransactionMessageMongoConfig();
        MongoClientFactoryBean clientFactoryBean = buildMongoClientFactoryBean(tccMongoConfig);
        try {
            clientFactoryBean.afterPropertiesSet();
            template = new MongoTemplate(Objects.requireNonNull(clientFactoryBean.getObject()), tccMongoConfig.getMongoDbName());
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
    }

    private MongoClientFactoryBean buildMongoClientFactoryBean(final MykitTransactionMessageMongoConfig mykitTransactionMessageMongoConfig) {
        MongoClientFactoryBean clientFactoryBean = new MongoClientFactoryBean();
        MongoCredential credential = MongoCredential.createScramSha1Credential(mykitTransactionMessageMongoConfig.getMongoUserName(),
                mykitTransactionMessageMongoConfig.getMongoDbName(),
                mykitTransactionMessageMongoConfig.getMongoUserPwd().toCharArray());
        clientFactoryBean.setCredentials(new MongoCredential[]{credential});
        List<String> urls = Splitter.on(",").trimResults().splitToList(mykitTransactionMessageMongoConfig.getMongoDbUrl());
        final ServerAddress[] sds = urls.stream().map(url -> {
            List<String> adds = Splitter.on(":").trimResults().splitToList(url);
            InetSocketAddress address = new InetSocketAddress(adds.get(0), Integer.parseInt(adds.get(1)));
            return new ServerAddress(address);
        }).collect(Collectors.toList()).toArray(new ServerAddress[]{});
        clientFactoryBean.setReplicaSetSeeds(sds);
        return clientFactoryBean;
    }

    @Override
    public void setSerializer(final ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    @SuppressWarnings("unchecked")
    private MykitTransaction buildByCache(final MongoAdapter cache) {
        MykitTransaction mythTransaction = new MykitTransaction();
        mythTransaction.setTransId(cache.getTransId());
        mythTransaction.setCreateTime(cache.getCreateTime());
        mythTransaction.setLastTime(cache.getLastTime());
        mythTransaction.setRetriedCount(cache.getRetriedCount());
        mythTransaction.setVersion(cache.getVersion());
        mythTransaction.setStatus(cache.getStatus());
        mythTransaction.setRole(cache.getRole());
        mythTransaction.setTargetClass(cache.getTargetClass());
        mythTransaction.setTargetMethod(cache.getTargetMethod());
        try {
            List<MykitTransactionParticipant> participants = (List<MykitTransactionParticipant>) objectSerializer.deSerialize(cache.getContents(), CopyOnWriteArrayList.class);
            mythTransaction.setMykitTransactionParticipants(participants);
        } catch (MykitException e) {
            LogUtil.error(LOGGER, "mongodb 反序列化异常:{}", e::getLocalizedMessage);
        }
        return mythTransaction;
    }
}
