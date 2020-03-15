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
import io.mykit.transaction.message.demo.springcloud.account.api.dto.AccountDto;
import io.mykit.transaction.message.demo.springcloud.account.api.entity.AccountDo;
import io.mykit.transaction.message.demo.springcloud.order.api.dto.InventoryDto;
import io.mykit.transaction.message.demo.springcloud.order.api.entity.InventoryDo;
import io.mykit.transaction.message.demo.springcloud.order.client.AccountClient;
import io.mykit.transaction.message.demo.springcloud.order.client.InventoryClient;
import io.mykit.transaction.message.demo.springcloud.order.entity.Order;
import io.mykit.transaction.message.demo.springcloud.order.enums.OrderStatusEnum;
import io.mykit.transaction.message.demo.springcloud.order.mapper.OrderMapper;
import io.mykit.transaction.message.demo.springcloud.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author binghe
 * @version 1.0.0
 * @description PaymentServiceImpl
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderMapper orderMapper;

    private final AccountClient accountClient;

    private final InventoryClient inventoryClient;

    /**
     * Instantiates a new Payment service.
     *
     * @param orderMapper     the order mapper
     * @param accountClient   the account client
     * @param inventoryClient the inventory client
     */
    @Autowired(required = false)
    public PaymentServiceImpl(OrderMapper orderMapper, AccountClient accountClient, InventoryClient inventoryClient) {
        this.orderMapper = orderMapper;
        this.accountClient = accountClient;
        this.inventoryClient = inventoryClient;
    }


    @Override
    @MykitTransactionMessage(destination = "")
    public void makePayment(Order order) {


        //检查数据 这里只是demo 只是demo 只是demo

        final AccountDo accountDO =
                accountClient.findByUserId(order.getUserId());

        if (accountDO.getBalance().compareTo(order.getTotalAmount()) <= 0) {
            throw new MykitRuntimeException("余额不足！");
        }

        final InventoryDo inventoryDO =
                inventoryClient.findByProductId(order.getProductId());

        if (inventoryDO.getTotalInventory() < order.getCount()) {
            throw new MykitRuntimeException("库存不足！");
        }

        order.setStatus(OrderStatusEnum.PAY_SUCCESS.getCode());
        orderMapper.update(order);
        //扣除用户余额
        AccountDto accountDTO = new AccountDto();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());

        accountClient.payment(accountDTO);

        //进入扣减库存操作
        InventoryDto inventoryDTO = new InventoryDto();
        inventoryDTO.setCount(order.getCount());
        inventoryDTO.setProductId(order.getProductId());
        inventoryClient.decrease(inventoryDTO);
    }
}
