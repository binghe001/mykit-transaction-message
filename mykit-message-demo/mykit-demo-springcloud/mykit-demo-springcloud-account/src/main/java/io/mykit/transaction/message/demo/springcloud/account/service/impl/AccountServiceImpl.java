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
package io.mykit.transaction.message.demo.springcloud.account.service.impl;

import io.mykit.transaction.message.annotation.MykitTransactionMessage;
import io.mykit.transaction.message.common.exception.MykitRuntimeException;
import io.mykit.transaction.message.demo.springcloud.account.api.dto.AccountDto;
import io.mykit.transaction.message.demo.springcloud.account.api.entity.AccountDo;
import io.mykit.transaction.message.demo.springcloud.account.api.service.AccountService;
import io.mykit.transaction.message.demo.springcloud.account.mapper.AccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author binghe
 * @version 1.0.0
 * @description AccountServiceImpl
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);


    private final AccountMapper accountMapper;

    /**
     * Instantiates a new Account service.
     *
     * @param accountMapper the account mapper
     */
    @Autowired(required = false)
    public AccountServiceImpl(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    /**
     * 扣款支付
     *
     * @param accountDto 参数dto
     * @return true
     */
    @Override
    @MykitTransactionMessage(destination = "account")
    @Transactional(rollbackFor = Exception.class)
    public boolean payment(AccountDto accountDto) {
        LOGGER.info("============springcloud执行付款接口===============");
        final AccountDo accountDo = accountMapper.findByUserId(accountDto.getUserId());
        if (accountDo.getBalance().compareTo(accountDto.getAmount()) <= 0) {
            throw new MykitRuntimeException("spring cloud account-service 资金不足！");
        }
        accountDo.setBalance(accountDo.getBalance().subtract(accountDto.getAmount()));
        accountDo.setUpdateTime(new Date());
        final int update = accountMapper.update(accountDo);
        if (update != 1) {
            throw new MykitRuntimeException("spring cloud account-service 资金不足！");
        }
        return Boolean.TRUE;
    }

    /**
     * 获取用户账户信息
     *
     * @param userId 用户id
     * @return AccountDO
     */
    @Override
    public AccountDo findByUserId(String userId) {
        return accountMapper.findByUserId(userId);
    }
}
