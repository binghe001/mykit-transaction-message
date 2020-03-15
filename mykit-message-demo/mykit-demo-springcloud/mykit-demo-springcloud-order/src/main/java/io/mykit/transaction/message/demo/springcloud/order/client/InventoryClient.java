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
package io.mykit.transaction.message.demo.springcloud.order.client;

import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.demo.springcloud.order.api.dto.InventoryDto;
import io.mykit.transaction.message.demo.springcloud.order.api.entity.InventoryDo;
import io.mykit.transaction.message.demo.springcloud.order.api.service.InventoryService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author binghe
 * @version 1.0.0
 * @description InventoryClient
 */
@FeignClient(value = "inventory-service")
public interface InventoryClient {

    /**
     * 库存扣减
     *
     * @param inventoryDto 实体对象
     * @return true 成功
     */
    @MykitTransactionMessage(destination = "inventory",target = InventoryService.class)
    @RequestMapping("/inventory-service/inventory/decrease")
    Boolean decrease(@RequestBody InventoryDto inventoryDto);


    /**
     * 获取商品库存
     *
     * @param productId 商品id
     * @return InventoryDO inventory do
     */
    @RequestMapping("/inventory-service/inventory/findByProductId")
    InventoryDo findByProductId(@RequestParam("productId") String productId);
}
