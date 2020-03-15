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
package io.mykit.transaction.message.demo.dubbo.inventory.api.service;

import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.demo.dubbo.inventory.api.dto.InventoryDto;
import io.mykit.transaction.message.demo.dubbo.inventory.api.entity.Inventory;

/**
 * @author binghe
 * @version 1.0.0
 * @description 库存接口
 */
public interface InventoryService {

    /**
     * 扣减库存操作
     * 这一个tcc接口
     *
     * @param inventoryDTO 库存DTO对象
     * @return true
     */
    @MykitTransactionMessage(destination = "ORDER_FLOW_BQ",tags = "inventory")
    Boolean decrease(InventoryDto inventoryDTO);



    /**
     * 获取商品库存信息
     * @param productId 商品id
     * @return Inventory
     */
    Inventory findByProductId(String productId);
}
