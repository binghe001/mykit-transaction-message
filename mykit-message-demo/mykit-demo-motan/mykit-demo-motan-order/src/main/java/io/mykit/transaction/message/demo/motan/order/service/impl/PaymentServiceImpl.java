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
package io.mykit.transaction.message.demo.motan.order.service.impl;

import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.demo.motan.account.api.dto.AccountDto;
import io.mykit.transaction.message.demo.motan.account.api.service.AccountService;
import io.mykit.transaction.message.demo.motan.inventory.api.dto.InventoryDto;
import io.mykit.transaction.message.demo.motan.inventory.api.service.InventoryService;
import io.mykit.transaction.message.demo.motan.order.entity.Order;
import io.mykit.transaction.message.demo.motan.order.enums.OrderStatusEnum;
import io.mykit.transaction.message.demo.motan.order.mapper.OrderMapper;
import io.mykit.transaction.message.demo.motan.order.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
@MotanService
public class PaymentServiceImpl implements PaymentService {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);


    private final OrderMapper orderMapper;

    @MotanReferer(basicReferer = "basicRefererConfig")
    private AccountService accountService;

    @MotanReferer(basicReferer = "basicRefererConfig")
    private InventoryService inventoryService;

    private static final String SUCCESS = "success";

    /**
     * Instantiates a new Payment service.
     *
     * @param orderMapper the order mapper
     */
    @Autowired(required = false)
    public PaymentServiceImpl(OrderMapper orderMapper
    ) {
        this.orderMapper = orderMapper;
    }


    @Override
    @MykitTransactionMessage()
    public void makePayment(Order order) {

        //做库存和资金账户的检验工作 这里只是demo 。。。

        LOGGER.debug("===check data over===");

        order.setStatus(OrderStatusEnum.PAY_SUCCESS.getCode());
        orderMapper.update(order);
        //扣除用户余额
        AccountDto accountDTO = new AccountDto();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());
        accountService.payment(accountDTO);
        //进入扣减库存操作
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setCount(order.getCount());
        inventoryDto.setProductId(order.getProductId());
        inventoryService.decrease(inventoryDto);
        LOGGER.debug("=============Myth分布式事务执行完成！=======");
    }
}
