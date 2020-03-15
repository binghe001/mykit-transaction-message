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
package io.mykit.transaction.message.demo.springcloud.order.service.impl;

import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.demo.springcloud.order.api.dto.InventoryDto;
import io.mykit.transaction.message.demo.springcloud.order.api.entity.InventoryDo;
import io.mykit.transaction.message.demo.springcloud.order.api.service.InventoryService;
import io.mykit.transaction.message.demo.springcloud.order.mapper.InventoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author binghe
 * @version 1.0.0
 * @description InventoryServiceImpl
 */
@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceImpl.class);


    private final InventoryMapper inventoryMapper;

    /**
     * Instantiates a new Inventory service.
     *
     * @param inventoryMapper the inventory mapper
     */
    @Autowired(required = false)
    public InventoryServiceImpl(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }


    /**
     * 扣减库存操作
     * 这一个tcc接口
     *
     * @param inventoryDto 库存DTO对象
     * @return true
     */
    @Override
    @MykitTransactionMessage(destination = "inventory")
    @Transactional(rollbackFor = Exception.class)
    public Boolean decrease(InventoryDto inventoryDto) {
        LOGGER.info("==========springcloud调用扣减库存decrease===========");
        final InventoryDo entity = inventoryMapper.findByProductId(inventoryDto.getProductId());

        if (entity.getTotalInventory() < inventoryDto.getCount()) {
            throw new MykitRuntimeException(" spring cloud inventory-service 库存不足!");
        }

        entity.setTotalInventory(entity.getTotalInventory() - inventoryDto.getCount());

        final int decrease = inventoryMapper.decrease(entity);
        if (decrease != 1) {
            throw new MykitRuntimeException("spring cloud inventory-service 库存不足!");
        }

        return true;
    }

    /**
     * 获取商品库存信息
     *
     * @param productId 商品id
     * @return InventoryDO
     */
    @Override
    public InventoryDo findByProductId(String productId) {
        return inventoryMapper.findByProductId(productId);
    }
}
