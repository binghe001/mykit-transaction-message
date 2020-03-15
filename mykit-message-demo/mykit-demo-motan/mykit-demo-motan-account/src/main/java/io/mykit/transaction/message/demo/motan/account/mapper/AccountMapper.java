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
package io.mykit.transaction.message.demo.motan.account.mapper;

import io.mykit.transaction.message.demo.motan.account.api.entity.AccountDo;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author binghe
 * @version 1.0.0
 * @description 操作账户的数据库
 */
public interface AccountMapper {

    /**
     * Update int.
     *
     * @param accountDo the account do
     * @return the int
     */
    @Update("update account set balance =#{balance}," +
            " update_time = #{updateTime}" +
            " where user_id =#{userId}  and  balance > 0  ")
    int update(AccountDo accountDo);

    /**
     * Find by user id account do.
     *
     * @param userId the user id
     * @return the account do
     */
    @Select("select * from account where user_id =#{userId}")
    AccountDo findByUserId(String userId);
}
