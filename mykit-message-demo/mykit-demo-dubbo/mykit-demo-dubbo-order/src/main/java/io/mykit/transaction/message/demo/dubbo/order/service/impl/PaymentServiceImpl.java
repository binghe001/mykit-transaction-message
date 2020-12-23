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
package io.mykit.transaction.message.demo.dubbo.order.service.impl;

import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.demo.dubbo.account.api.dto.AccountDto;
import io.mykit.transaction.message.demo.dubbo.account.api.entity.AccountDo;
import io.mykit.transaction.message.demo.dubbo.account.api.service.AccountService;
import io.mykit.transaction.message.demo.dubbo.inventory.api.dto.InventoryDto;
import io.mykit.transaction.message.demo.dubbo.inventory.api.entity.Inventory;
import io.mykit.transaction.message.demo.dubbo.inventory.api.service.InventoryService;
import io.mykit.transaction.message.demo.dubbo.order.entity.Order;
import io.mykit.transaction.message.demo.dubbo.order.enums.OrderStatusEnum;
import io.mykit.transaction.message.demo.dubbo.order.mapper.OrderMapper;
import io.mykit.transaction.message.demo.dubbo.order.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author binghe
 * @version 1.0.0
 * @description 支付业务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final OrderMapper orderMapper;

    private final AccountService accountService;

    private final InventoryService inventoryService;

    @Autowired(required = false)
    public PaymentServiceImpl(OrderMapper orderMapper,
                              AccountService accountService,
                              InventoryService inventoryService) {
        this.orderMapper = orderMapper;
        this.accountService = accountService;
        this.inventoryService = inventoryService;
    }

    @Override
    @MykitTransactionMessage
    public void makePayment(Order order) {
        //做库存和资金账户的检验工作 这里只是demo 。。。
        final AccountDo accountDO = accountService.findByUserId(order.getUserId());
        if (accountDO.getBalance().compareTo(order.getTotalAmount()) <= 0) {
            return;
        }
        final Inventory inventory = inventoryService.findByProductId(order.getProductId());
        if (inventory.getTotalInventory() < order.getCount()) {
            return;
        }
        order.setStatus(OrderStatusEnum.PAY_SUCCESS.getCode());
        orderMapper.update(order);
        //扣除用户余额
        AccountDto accountDTO = new AccountDto();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());
        accountService.payment(accountDTO);
        //进入扣减库存操作
        InventoryDto inventoryDTO = new InventoryDto();
        inventoryDTO.setCount(order.getCount());
        inventoryDTO.setProductId(order.getProductId());
        inventoryService.decrease(inventoryDTO);
        LOGGER.debug("=============mykit-transaction-message分布式事务执行完成！=======");
    }
}
