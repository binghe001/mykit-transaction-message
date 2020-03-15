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

import io.mykit.transaction.message.common.utils.IdWorkerUtils;
import io.mykit.transaction.message.demo.springcloud.order.entity.Order;
import io.mykit.transaction.message.demo.springcloud.order.enums.OrderStatusEnum;
import io.mykit.transaction.message.demo.springcloud.order.mapper.OrderMapper;
import io.mykit.transaction.message.demo.springcloud.order.service.OrderService;
import io.mykit.transaction.message.demo.springcloud.order.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author binghe
 * @version 1.0.0
 * @description OrderServiceImpl
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderMapper orderMapper;

    private final PaymentService paymentService;

    /**
     * Instantiates a new Order service.
     *
     * @param orderMapper    the order mapper
     * @param paymentService the payment service
     */
    @Autowired(required = false)
    public OrderServiceImpl(OrderMapper orderMapper, PaymentService paymentService) {
        this.orderMapper = orderMapper;
        this.paymentService = paymentService;
    }


    @Override
    public String orderPay(Integer count, BigDecimal amount) {
        final Order order = buildOrder(count, amount);
        final int rows = orderMapper.save(order);

        if (rows > 0) {
            paymentService.makePayment(order);
        }


        return "success";
    }

    private Order buildOrder(Integer count, BigDecimal amount) {
        LOGGER.debug("构建订单对象");
        Order order = new Order();
        order.setCreateTime(new Date());
        order.setNumber(IdWorkerUtils.getInstance().createUUID());
        //demo中的表里只有商品id为 1的数据
        order.setProductId("1");
        order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        order.setTotalAmount(amount);
        order.setCount(count);
        //demo中 表里面存的用户id为10000
        order.setUserId("10000");
        return order;
    }
}
