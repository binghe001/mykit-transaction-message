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
package io.mykit.transaction.message.demo.motan.inventory.service;

import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.demo.motan.inventory.api.dto.InventoryDto;
import io.mykit.transaction.message.demo.motan.inventory.api.entity.Inventory;
import io.mykit.transaction.message.demo.motan.inventory.api.service.InventoryService;
import io.mykit.transaction.message.demo.motan.inventory.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author binghe
 * @version 1.0.0
 * @description 库存业务实现
 */
@MotanService
public class InventoryServiceImpl implements InventoryService {

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
     *
     * @param inventoryDto 库存DTO对象
     * @return true
     */
    @Override
    @MykitTransactionMessage(destination = "inventory")
    public Boolean decrease(InventoryDto inventoryDto) {
        final Inventory entity = findByProductId(inventoryDto.getProductId());
        if (entity.getTotalInventory() < inventoryDto.getCount()) {
            throw new MykitRuntimeException("motan  库存不足");
        }
        entity.setTotalInventory(entity.getTotalInventory() - inventoryDto.getCount());
        final int decrease = inventoryMapper.decrease(entity);
        if (decrease != 1) {
            throw new MykitRuntimeException("库存不足");
        }
        return true;
    }

    /**
     * 获取商品库存信息
     *
     * @param productId 商品id
     * @return Inventory
     */
    @Override
    public Inventory findByProductId(String productId) {
        return inventoryMapper.findByProductId(productId);
    }
}
