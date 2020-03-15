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
package io.mykit.transaction.message.demo.dubbo.inventory.mapper;

import io.mykit.transaction.message.demo.dubbo.inventory.api.entity.Inventory;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author binghe
 * @version 1.0.0
 * @description 操作库存数据库
 */
public interface InventoryMapper {

    /**
     * 库存扣减
     *
     * @param inventory 实体对象
     * @return rows
     */
    @Update("update inventory set total_inventory =#{totalInventory}" +
            " where product_id =#{productId}  and  total_inventory >0  ")
    int decrease(Inventory inventory);


    /**
     * 根据商品id找到库存信息
     *
     * @param productId 商品id
     * @return Inventory
     */
    @Select("select * from inventory where product_id =#{productId}")
    Inventory findByProductId(String productId);
}
