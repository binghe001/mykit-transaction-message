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
import io.mykit.transaction.message.common.constant.CommonConstant;
import io.mykit.transaction.message.common.enums.MykitTransactionMessageStatusEnum;
import io.mykit.transaction.message.common.exception.MykitException;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.common.serializer.ObjectSerializer;
import io.mykit.transaction.message.common.utils.FileUtils;
import io.mykit.transaction.message.common.utils.RepositoryConvertUtils;
import io.mykit.transaction.message.common.utils.RepositoryPathUtils;
import io.mykit.transaction.message.core.spi.MykitCoordinatorRepository;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author binghe
 * @version 1.0.0
 * @description FileCoordinatorRepository
 */
@MykitTransactionMessageSPI("file")
public class FileCoordinatorRepository implements MykitCoordinatorRepository {

    private String filePath;

    private ObjectSerializer serializer;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public void setSerializer(final ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public int create(final MykitTransaction transaction) {
        writeFile(transaction);
        return CommonConstant.SUCCESS;
    }

    @Override
    public int remove(final String id) {
        String fullFileName = RepositoryPathUtils.getFullFileName(filePath, id);
        File file = new File(fullFileName);
        return file.exists() && file.delete() ? CommonConstant.SUCCESS : CommonConstant.ERROR;
    }

    @Override
    public int update(final MykitTransaction transaction) throws MykitRuntimeException {
        transaction.setLastTime(new Date());
        transaction.setVersion(transaction.getVersion() + 1);
        transaction.setRetriedCount(transaction.getRetriedCount() + 1);
        writeFile(transaction);
        return CommonConstant.SUCCESS;
    }

    @Override
    public void updateFailTransaction(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        try {
            final String fullFileName = RepositoryPathUtils.getFullFileName(filePath, mythTransaction.getTransId());
            mythTransaction.setLastTime(new Date());
            FileUtils.writeFile(fullFileName, RepositoryConvertUtils.convert(mythTransaction, serializer));
        } catch (MykitException e) {
            throw new MykitRuntimeException("update exception！");
        }
    }

    @Override
    public void updateParticipant(final MykitTransaction mythTransaction) throws MykitRuntimeException {
        try {
            final String fullFileName = RepositoryPathUtils.getFullFileName(filePath, mythTransaction.getTransId());
            final File file = new File(fullFileName);
            final CoordinatorRepositoryAdapter adapter = readAdapter(file);
            if (Objects.nonNull(adapter)) {
                adapter.setContents(serializer.serialize(mythTransaction.getMykitTransactionParticipants()));
            }
            FileUtils.writeFile(fullFileName, serializer.serialize(adapter));
        } catch (Exception e) {
            throw new MykitRuntimeException("update exception！");
        }

    }

    @Override
    public int updateStatus(final String id, final Integer status) throws MykitRuntimeException {
        try {
            final String fullFileName = RepositoryPathUtils.getFullFileName(filePath, id);
            final File file = new File(fullFileName);

            final CoordinatorRepositoryAdapter adapter = readAdapter(file);
            if (Objects.nonNull(adapter)) {
                adapter.setStatus(status);
            }
            FileUtils.writeFile(fullFileName, serializer.serialize(adapter));
            return CommonConstant.SUCCESS;
        } catch (Exception e) {
            throw new MykitRuntimeException("更新数据异常！");
        }

    }

    @Override
    public MykitTransaction findByTransId(final String transId) {
        String fullFileName = RepositoryPathUtils.getFullFileName(filePath, transId);
        File file = new File(fullFileName);
        try {
            return readTransaction(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<MykitTransaction> listAllByDelay(final Date date) {
        final List<MykitTransaction> mythTransactionList = listAll();
        return mythTransactionList.stream()
                .filter(tccTransaction -> tccTransaction.getLastTime().compareTo(date) < 0)
                .filter(mythTransaction -> mythTransaction.getStatus() == MykitTransactionMessageStatusEnum.BEGIN.getCode())
                .collect(Collectors.toList());
    }

    private List<MykitTransaction> listAll() {
        List<MykitTransaction> transactionRecoverList = Lists.newArrayList();
        File path = new File(filePath);
        File[] files = path.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    MykitTransaction transaction = readTransaction(file);
                    transactionRecoverList.add(transaction);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return transactionRecoverList;
    }

    @Override
    public void init(final String modelName, final MykitTransactionMessageConfig mykitTransactionMessageConfig) {
        filePath = RepositoryPathUtils.buildFilePath(modelName);
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.mkdirs();
        }
    }

    private MykitTransaction readTransaction(final File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] content = new byte[(int) file.length()];
            fis.read(content);
            return RepositoryConvertUtils.transformBean(content, serializer);
        }
    }

    private CoordinatorRepositoryAdapter readAdapter(final File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] content = new byte[(int) file.length()];
            fis.read(content);
            return serializer.deSerialize(content, CoordinatorRepositoryAdapter.class);
        }
    }

    private void writeFile(final MykitTransaction transaction) throws MykitRuntimeException {
        for (; ; ) {
            if (makeDirIfNecessary()) {
                break;
            }
        }
        try {
            String fileName = RepositoryPathUtils.getFullFileName(filePath, transaction.getTransId());
            FileUtils.writeFile(fileName, RepositoryConvertUtils.convert(transaction, serializer));
        } catch (Exception e) {
            throw new MykitRuntimeException("fail to write transaction to local storage", e);
        }
    }

    private boolean makeDirIfNecessary() throws MykitRuntimeException {
        if (!initialized.getAndSet(true)) {
            File rootDir = new File(filePath);
            boolean isExist = rootDir.exists();
            if (!isExist) {
                if (rootDir.mkdir()) {
                    return true;
                } else {
                    throw new MykitRuntimeException(String.format("fail to make root directory, path:%s.", filePath));
                }
            } else {
                if (rootDir.isDirectory()) {
                    return true;
                } else {
                    throw new MykitRuntimeException(String.format("the root path is not a directory, please check again, path:%s.", filePath));
                }
            }
        }
        return true;// 已初始化目录，直接返回true
    }
}
