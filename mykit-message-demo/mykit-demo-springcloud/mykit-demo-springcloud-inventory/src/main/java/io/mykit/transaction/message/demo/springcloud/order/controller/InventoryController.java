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
package io.mykit.transaction.message.demo.springcloud.order.controller;

import io.mykit.transaction.message.demo.springcloud.order.api.dto.InventoryDto;
import io.mykit.transaction.message.demo.springcloud.order.api.entity.InventoryDo;
import io.mykit.transaction.message.demo.springcloud.order.api.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author binghe
 * @version 1.0.0
 * @description InventoryController
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Instantiates a new Inventory controller.
     *
     * @param inventoryService the inventory service
     */
    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Decrease boolean.
     *
     * @param inventoryDto the inventory dto
     * @return the boolean
     */
    @RequestMapping("/decrease")
    public Boolean decrease(@RequestBody InventoryDto inventoryDto) {
        return inventoryService.decrease(inventoryDto);
    }

    /**
     * Find by product id inventory do.
     *
     * @param productId the product id
     * @return the inventory do
     */
    @RequestMapping("/findByProductId")
    public InventoryDo findByProductId(@RequestParam("productId") String productId) {
        return inventoryService.findByProductId(productId);
    }
}
