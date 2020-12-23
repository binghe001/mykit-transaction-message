# 作者及联系方式
作者：冰河  
微信：sun_shine_lyz  
QQ：2711098650  
微信公众号： 冰河技术

# 项目简述
基于猫大人Myth框架演化而来的最终消息一致的分布式事务框架mykit-transaction-message，站在巨人的肩膀上看的更远，使用disruptor极大的提升了框架的性能。目前已使用到生产环境，经受住了分布式事务场景的考验。  
* 目前支持的消息中间件有：AliyunMQ、ActiveMQ、Kafka、RabbitMQ、RocketMQ。  
* 目前支持的RPC调用方式有：Dubbo、Motan、SpringCloud（BRPC与GRPC调用方式正在开发中...）。  
* 目前支持的SpringBoot整合方式有：Dubbo、Motan和SpringCloud。  


# 项目结构说明
* mykit-message-admin：分布式事务框架后台管理系统，主要用来动态配置和管理分布式事务，目前开发中；  
* mykit-message-annotation：分布式事务核心注解；  
* mykit-message-common：分布式事务通用工具工程，封装各种工具类、实现事务解析、异常、序列化、Redis缓存等功能；  
* mykit-message-core：分布式事务核心工程，实现分布式事务的拦截器、调度器、SPI、disruptor等核心功能，并实现消息的通用投递和消息逻辑；  
* mykit-message-mq：封装各种消息中间件，目前支持的消息中间件有：AliyunMQ、ActiveMQ、Kafka、RabbitMQ、RocketMQ；  
* mykit-message-rpc：封装各种RPC的实现，目前支持的RPC调用方式有：Dubbo、Motan、SpringCloud；  
* mykit-message-springboot-starter：整合SpringBoot，目前支持整合SpringBoot的方式有：Dubbo、Motan、SpringCloud。
* mykit-message-demo：分布式事务的测试工程模块，以典型的下单、支付、扣减库存场景来测试分布式事务，目前支持基于Dubbo、Motan和SpringCloud三种实现方式。  
测试时，根据自身实际情况，创建数据库，并将mykit-message-demo下的sql目录下的mykit-mysql-demo.sql脚本导入到数据库，同时修改对应的数据库配置。

# 扫一扫关注微信公众号

**你在刷抖音，玩游戏的时候，别人都在这里学习，成长，提升，人与人最大的差距其实就是思维。你可能不信，优秀的人，总是在一起。** 
  
扫一扫关注冰河技术微信公众号  
![微信公众号](https://img-blog.csdnimg.cn/20200906013715889.png)  
 
