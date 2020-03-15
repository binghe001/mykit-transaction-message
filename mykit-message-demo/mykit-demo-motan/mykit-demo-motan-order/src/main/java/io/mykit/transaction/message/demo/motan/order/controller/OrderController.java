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
package io.mykit.transaction.message.demo.motan.order.controller;

import io.mykit.transaction.message.demo.motan.order.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author binghe
 * @version 1.0.0
 * @description 订单接口
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    /**
     * Instantiates a new Order controller.
     *
     * @param orderService the order service
     */
    @Autowired(required = false)
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    /**
     * Order pay string.
     *
     * @param count  the count
     * @param amount the amount
     * @return the string
     */
    @PostMapping(value = "/orderPay")
    @ApiOperation(value = "订单支付接口（注意这里模拟的是创建订单并进行支付扣减库存等操作）")
    public String orderPay(@RequestParam(value = "count") Integer count,
                           @RequestParam(value = "amount") BigDecimal amount) {

        return orderService.orderPay(count, amount);

    }
}
