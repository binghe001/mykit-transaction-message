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

import com.google.common.collect.Lists;
import io.mykit.transaction.message.annotation.MykitTransactionMessageSPI;
import io.mykit.transaction.message.common.bean.adapter.CoordinatorRepositoryAdapter;
import io.mykit.transaction.message.common.bean.entity.MykitTransaction;
import io.mykit.transaction.message.common.config.MykitTransactionMessageConfig;
import io.mykit.transaction.message.common.config.MykitTransactionMessageZookeeperConfig;
import io.mykit.transaction.message.common.constant.CommonConstant;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;
import io.mykit.transaction.message.common.utils.LogUtil;
import io.mykit.transaction.message.common.utils.RepositoryConvertUtils;
import io.mykit.transaction.message.common.utils.RepositoryPathUtils;
import io.mykit.transaction.message.core.spi.MykitCoordinatorRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author binghe
 * @version 1.0.0
 * @description ZookeeperCoordinatorRepository
 */
@MykitTransactionMessageSPI("zookeeper")
public class ZookeeperCoordinatorRepository implements MykitCoordinatorRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperCoordinatorRepository.class);

    private static volatile ZooKeeper zooKeeper;

    private static final CountDownLatch LATCH = new CountDownLatch(1);

    private ObjectSerializer objectSerializer;

    private String rootPathPrefix = "/mykit";

    @Override
    public int create(final MykitTransaction mykitTransaction) {
        try {
            zooKeeper.create(buildRootPath(mykitTransaction.getTransId()),
                    RepositoryConvertUtils.convert(mykitTransaction, objectSerializer),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return CommonConstant.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return CommonConstant.ERROR;
        }
    }

    @Override
    public int remove(final String transId) {
        try {
            zooKeeper.delete(buildRootPath(transId), -1);
            return CommonConstant.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return CommonConstant.ERROR;
        }
    }

    @Override
    public int update(final MykitTransaction mykitTransaction) throws MykitRuntimeException {
        try {
            mykitTransaction.setLastTime(new Date());
            mykitTransaction.setVersion(mykitTransaction.getVersion() + 1);
            zooKeeper.setData(buildRootPath(mykitTransaction.getTransId()),
                    RepositoryConvertUtils.convert(mykitTransaction, objectSerializer), -1);
            return CommonConstant.SUCCESS;
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
    }

    @Override
    public void updateFailTransaction(final MykitTransaction mykitTransaction) throws MykitRuntimeException {
        update(mykitTransaction);
    }

    @Override
    public void updateParticipant(final MykitTransaction mykitTransaction) throws MykitRuntimeException {
        final String path = RepositoryPathUtils.buildZookeeperRootPath(rootPathPrefix, mykitTransaction.getTransId());
        try {
            byte[] content = zooKeeper.getData(path, false, new Stat());
            if (content != null) {
                final CoordinatorRepositoryAdapter adapter =
                        objectSerializer.deSerialize(content, CoordinatorRepositoryAdapter.class);
                adapter.setContents(objectSerializer.serialize(mykitTransaction.getMykitTransactionParticipants()));
                //TODO issue 28 重复创建node ==> 异常
                //zooKeeper.create(path, objectSerializer.serialize(adapter),
                //        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zooKeeper.setData(path, objectSerializer.serialize(adapter), -1);
            }
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
    }

    @Override
    public int updateStatus(final String id, final Integer status) throws MykitRuntimeException {
        final String path = RepositoryPathUtils.buildZookeeperRootPath(rootPathPrefix, id);
        try {
            byte[] content = zooKeeper.getData(path, false, new Stat());
            if (content != null) {
                final CoordinatorRepositoryAdapter adapter =
                        objectSerializer.deSerialize(content, CoordinatorRepositoryAdapter.class);
                adapter.setStatus(status);
                //TODO issue 28 重复创建node ==> 异常
                //zooKeeper.create(path,
                //        objectSerializer.serialize(adapter),
                //        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zooKeeper.setData(path, objectSerializer.serialize(adapter), -1);
            }
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
        return CommonConstant.SUCCESS;
    }

    @Override
    public MykitTransaction findByTransId(final String transId) {
        try {
            Stat stat = new Stat();
            byte[] content = zooKeeper.getData(buildRootPath(transId), false, stat);
            return RepositoryConvertUtils.transformBean(content, objectSerializer);
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
        List<MykitTransaction> transactionRecovers = Lists.newArrayList();
        List<String> zNodePaths;
        try {
            zNodePaths = zooKeeper.getChildren(rootPathPrefix, false);
        } catch (Exception e) {
            throw new MykitRuntimeException(e);
        }
        if (CollectionUtils.isNotEmpty(zNodePaths)) {
            transactionRecovers = zNodePaths.stream()
                    .filter(StringUtils::isNoneBlank)
                    .map(zNodePath -> {
                        try {
                            byte[] content = zooKeeper.getData(buildRootPath(zNodePath), false, new Stat());
                            return RepositoryConvertUtils.transformBean(content, objectSerializer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.toList());
        }
        return transactionRecovers;
    }

    @Override
    public void init(final String modelName, final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        rootPathPrefix = RepositoryPathUtils.buildZookeeperPathPrefix(modelName);
        connect(mykitTransactionMessageConfig.getMykitTransactionMessageZookeeperConfig());
    }

    private void connect(final MykitTransactionMessageZookeeperConfig zookeeperConfig) {
        try {
            zooKeeper = new ZooKeeper(zookeeperConfig.getHost(), zookeeperConfig.getSessionTimeOut(), watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    // 放开闸门, wait在connect方法上的线程将被唤醒
                    LATCH.countDown();
                }
            });
            LATCH.await();
            Stat stat = zooKeeper.exists(rootPathPrefix, false);
            if (stat == null) {
                zooKeeper.create(rootPathPrefix, rootPathPrefix.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            LogUtil.error(LOGGER, "zookeeper init error please check you config!:{}", e::getMessage);
            throw new MykitRuntimeException(e);
        }

    }

    @Override
    public void setSerializer(final ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private String buildRootPath(final String id) {
        return RepositoryPathUtils.buildZookeeperRootPath(rootPathPrefix, id);
    }
}
