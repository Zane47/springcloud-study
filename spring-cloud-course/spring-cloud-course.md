# 介绍

基于spring cloud开发课程查询功能

spring cloud的组件学习

模块之间调用. 断路器. 网关

Spring Cloud简洁 -> 目整体设计 -> 课程列表模块开发 -> 课程价格模块开发(重点, 模块调用) -> 服务注册与发现Eureka -> 服务之间调用Feign(简单, 可以应对地址变化情况) -> 负载均衡Ribbon -> 熔断器Hystrix(兜底) -> 网关Zuul -> 整体测试 -> 项目总结

## Spring Cloud简介

* 成熟的微服务框架, 定位为开发人员提供工具, 以快速构建分布式系统

| 核心组件     | Spring Cloud                                                 |
| ------------ | ------------------------------------------------------------ |
| 服务注册中心 | Spring Cloud Netflix Eureka                                  |
| 服务调用方式 | REST API(Spring Cloud推荐的http方式), Feign(服务调用), Ribbon(负载均衡) |
| 服务网关     | Spring Cloud Netflix Zuul. <br />1. 不同模块的配合流畅 <br />2. 对外部用户就暴露网关即可, 更加安全 <br />3. 可以设置过滤器(鉴权)等功能 |
| 断路器       | Spring Cloud Netflix Hystrix                                 |

核心: 搭建Spring Cloud必不可少的组件, 或者说是90%情况都需要使用的组件

## 项目整体设计

课程列表模块和课程价格模块









# 















































