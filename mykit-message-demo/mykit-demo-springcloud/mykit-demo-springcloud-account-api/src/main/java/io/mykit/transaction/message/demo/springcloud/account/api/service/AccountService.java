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
package io.mykit.transaction.message.demo.springcloud.account.api.service;

import io.mykit.transaction.message.demo.springcloud.account.api.dto.AccountDto;
import io.mykit.transaction.message.demo.springcloud.account.api.entity.AccountDo;

/**
 * @author binghe
 * @version 1.0.0
 * @description 账户业务接口
 */
public interface AccountService {
    /**
     * 扣款支付
     *
     * @param accountDto 参数dto
     * @return true
     */
    boolean payment(AccountDto accountDto);

    /**
     * 获取用户账户信息
     * @param userId 用户id
     * @return AccountDO
     */
    AccountDo findByUserId(String userId);
}
