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
package io.mykit.transaction.message.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitTransactionMessage注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MykitTransactionMessage {

    /**
     * The destination name for this listener, resolved through the container-wide
     * 消息队列的唯一标识(在rocketmq或者aliyunmq中是topic).
     *
     * @return destination string
     */
    String destination() default "";

    /**
     * rocketmq特有的tag区分方式,tags的值需要完全满足rocketmq规则.
     *
     * @return tags string
     */
    String tags() default "";

    /**
     * 目标接口类
     * 如果是springcloud用户，需要指定目标的接口服务
     * （因为springcloud是http的请求，通过反射序列化方式没办法调用，所有加了这个属性）
     * 如果是dubbo用户 则不需要指定
     * 如果是motan用户 则不需要指定.
     *
     * @return Class class
     */
    Class target() default Object.class;

    /**
     * 目标接口方法名称
     * 如果是springcloud用户，需要指定目标的方法名称
     * （因为springcloud是http的请求，通过反射序列化方式没办法调用，所有加了这个属性）
     * 如果是dubbo用户 则不需要指定
     * 如果是motan用户 则不需要指定.
     *
     * @return String string
     */
    String targetMethod() default "";

    /**
     * 是否有事务 这里具体指的是发起方是否有进行数据库的操作（是否有事务操作）.
     *
     * @return PropagationEnum propagation enum
     */
    PropagationEnum propagation() default PropagationEnum.PROPAGATION_REQUIRED;

    /**
     * mq 消息模式.
     *
     * @return MessageTypeEnum message type enum
     */
    MessageTypeEnum pattern() default MessageTypeEnum.P2P;
}
