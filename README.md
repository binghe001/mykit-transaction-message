### 我是冰河

- :dog: 互联资深技术专家，数据库技术专家，分布式与微服务架构专家，全网45万+粉互联网技术博主。
- :bus: 作品：[`BingheGuide | 冰河指南`](https://github.com/binghe001/BingheGuide) | [`《深入理解高并发编程：核心原理与案例实战》`](https://github.com/binghe001/mykit-concurrent-principle) | [`《深入理解高并发编程：JDK核心技术》`](https://github.com/binghe001/mykit-concurrent-jdk) | [`数据同步`](https://github.com/binghe001/mykit-data) | [`Spring核心技术`](https://github.com/binghe001/spring-annotation-book) | [`分布式限流`](https://github.com/binghe001/mykit-ratelimiter) | [`分布式锁`](https://github.com/binghe001/mykit-lock) | [`分布式缓存`](https://github.com/binghe001/mykit-cache) | [`异步并行框架`](https://github.com/binghe001/mykit-async) | [`分布式事务`](https://github.com/binghe001/mykit-transaction-message) | [`简易版IM`](https://github.com/binghe001/mykit-chat) | [`微信SDK`](https://github.com/binghe001/mykit-wechat-sdk) | [`延迟队列`](https://github.com/binghe001/mykit-delay) | [`分布式ID`](https://github.com/binghe001/mykit-serial) | [更多搜索...](https://github.com/binghe001?tab=repositories)
- :seedling: 干货：[公众号『 冰河技术 』](https://img-blog.csdnimg.cn/20210426115714643.jpg)
- :pencil: 博客：[binghe.gitcode.host](https://binghe.gitcode.host/) - 硬核文章，应有尽有！
- :tv: 视频：[B站 冰河技术](https://space.bilibili.com/517638832)
- :love_letter: 微信：[hacker_binghe](https://binghe.gitcode.host/images/personal/hacker_binghe.jpg) - 备注来意
- :feet: 我的知识星球：[手写企业级中间件项目、大厂高并发秒杀系统、并发编程、性能调优、框架源码、分布式、微服务、1对1解答、答辩晋升技巧、定期直播](https://binghe.gitcode.host/md/starball/2023-01-01-2023%E6%98%9F%E7%90%83%E6%96%B0%E5%B9%B4%E8%A7%84%E5%88%92.html)

### 今年的努力 ✨

<img align="" height="137px" src="https://github-readme-stats.vercel.app/api?username=binghe001&hide_title=true&hide_border=true&show_icons=true&include_all_commits=true&line_height=21&bg_color=0,EC6C6C,FFD479,FFFC79,73FA79&theme=graywhite&locale=cn" /><img align="" height="137px" src="https://github-readme-stats.vercel.app/api/top-langs/?username=binghe001&hide_title=true&hide_border=true&layout=compact&bg_color=0,73FA79,73FDFF,D783FF&theme=graywhite&locale=cn" />

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
 
