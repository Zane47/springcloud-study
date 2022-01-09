# 项目介绍

基于spring cloud开发课程查询功能

spring cloud的组件学习

模块之间调用. 断路器. 网关

Spring Cloud简洁 -> 目整体设计 -> 课程列表模块开发 -> 课程价格模块开发(重点, 模块调用) -> 服务注册与发现Eureka -> 服务之间调用Feign(简单, 可以应对地址变化情况) -> 负载均衡Ribbon
-> 熔断器Hystrix(兜底) -> 网关Zuul -> 整体测试 -> 项目总结

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

模块:

* 课程列表模块
* 课程价格模块

接口:

* 课程列表接口. 从db中读取内容, 做处理
* 单个课程价格. 入参课程id,
* 整合课程列表和价格. 远程调用获取所有课程列表(第一个模块调用), 通过id查到价格再补充到原来的列表

## 系统数据流向

![image-20220108155718609](img/spring-cloud-course/image-20220108155718609.png)

## 表设计

name -> course_name

![image-20220108160731011](img/spring-cloud-course/image-20220108160731011.png)

![image-20220108160821261](img/spring-cloud-course/image-20220108160821261.png)

## 新建多模块项目

1. Spring Initializr新建项目spring-cloud-course. 2.1.12.RELEASE
1. 删除spring-cloud-course的src文件目录

3. 右击spring-cloud-course, 新建module, maven项目

![image-20220109091040695](img/spring-cloud-course/image-20220109091040695.png)

name: course-service: 存放课程服务

<img src="img/spring-cloud-course/image-20220109091121336.png" alt="image-20220109091121336" style="zoom:67%;" />

4. 删除course-service中的src. 同时在此module上右键新建模块course-list

注意这里的parent要选择course-service

<img src="img/spring-cloud-course/image-20220109091250888.png" alt="image-20220109091250888" style="zoom:67%;" />

5. 一样的流程, 再在course-service下新建模块course-price

parent仍未course-service

---

最后的项目结构

![image-20220109091632943](img/spring-cloud-course/image-20220109091632943.png)

**Spring Cloud模块都是一个个spring boot项目**

# 课程列表模块course-list

## 基本设置流程

### 添加依赖+设置springboot启动文件

* 项目为springboot项目, 需要添加相对应的依赖. 

* 同时引入数据库mybatis相关依赖. 

* 还需要添加springboot maven项目的插件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>course-service</artifactId>
        <groupId>com.example</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>course-list</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- springboot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- mybatis + db -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.1</version>
        </dependency>
        
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

为项目添加启动文件CourseListApplication.java

```java
package com.imooc.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类
 */
@SpringBootApplication
public class CourseListApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseListApplication.class, args);
    }
}
```

### 添加配置文件, application.properties

端口

数据库driver + url + name +pwd

日志

应用名称

```properties
#port
server.port=8081

#db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.datasource.url=jdbc:mysql://114.55.64.149:3318/springcloudlearn?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true
spring.datasource.username=root
spring.datasource.password=

#log
logging.pattern.console=logging.pattern.console=%clr(%d{${LOG_DATEFORMAT_PATTERN:HH:mm:ss.SSS}}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:%wEx}

#application name
spring.application.name=course-list
```

### 书写基本结构

<img src="img/spring-cloud-course/image-20220109103046479.png" alt="image-20220109103046479" style="zoom:50%;" />

1. 新建Course entity

```java
package com.imooc.course.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * course 实体类
 */
@Setter
@Getter
public class Course {
    Integer id;
    Integer courseId;
    String courseName;
    Integer valid;
}
```

2. Controller层

```java
package com.imooc.course.controller;

import com.imooc.course.entity.Course;
import com.imooc.course.service.CourseListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourseListController {

    @Autowired
    private CourseListService courseListService;
	
    @GetMapping("/courses")
    public List<Course> getCourseList() {
        return courseListService.getCourseList();
    }
}
```

3. Service层

```java
package com.imooc.course.service;


import com.imooc.course.entity.Course;

import java.util.List;


public interface CourseListService {
    public List<Course> getCourseList();
}
```

```java
package com.imooc.course.service.impl;

import com.imooc.course.dao.CourseMapper;
import com.imooc.course.entity.Course;
import com.imooc.course.service.CourseListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程服务实现类
 */
@Service
public class CourseListServiceImpl implements CourseListService {

    @Autowired
    private CourseMapper courseMapper;


    @Override
    public List<Course> getCourseList() {
        return courseMapper.findValidCourse();
    }
}
```

4. dao层

```java
package com.imooc.course.dao;

import com.imooc.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程mapper类
 */
@Mapper
@Repository
public interface CourseMapper {

    @Select("select * from course where valid = 1")
    public List<Course> findValidCourse();

}
```

## 运行查看效果

### 驼峰对应

![image-20220109103948893](img/spring-cloud-course/image-20220109103948893.png)

可以看到courseId没有赋值, 需要开启驼峰对应

在application.properties中添加配置

```properties
# mybatis Camel-Case
mybatis.configuration.map-underscore-to-camel-case=true
```

重启后查看

![image-20220109104047176](img/spring-cloud-course/image-20220109104047176.png)

### 实体类序列化

上文中直接使用lombok设置getter和setter

如果不适用lombok, 会报错

```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class com.imooc.course.entity.Course and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: java.util.ArrayList[0])
```

所以如果不适用lombok, 就让实体类实现接口Serializable并且实现getter和setter

```java
package com.imooc.course.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * course 实体类
 */

public class Course implements Serializable {
    Integer id;
    Integer courseId;
    String courseName;
    Integer valid;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }
}
```

这里仍然使用按照习惯使用lombok

```java
package com.imooc.course.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * course 实体类
 */
@Getter
@Setter
@ToString
public class Course /*implements Serializable*/ {
    Integer id;
    Integer courseId;
    String courseName;
    Integer valid;
}

```

# 课程价格模块course-price

## 基本配置

### 配置依赖和启动类

一样是springboot应用, 与course-list模块一样配置依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>course-service</artifactId>
        <groupId>com.example</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>course-price</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- springboot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- mybatis + db -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.1</version>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

```java
package com.imooc.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoursePriceApplication {
    public static void main(String[] args) {

        SpringApplication.run(CoursePriceApplication.class, args);
    }
}
```

### application.properties

course-list中配置文件类似.

修改端口号, 修改application name

```properties
# port
server.port=8082

# db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://114.55.64.149:3318/springcloudlearn?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true
spring.datasource.username=root
spring.datasource.password=

# mybatis Camel-Case
mybatis.configuration.map-underscore-to-camel-case=true

# log
logging.pattern.console=logging.pattern.console=%clr(%d{${LOG_DATEFORMAT_PATTERN:HH:mm:ss.SSS}}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:%wEx}

#application name
spring.application.name=course-price
```

### 根据courseId查询课程价格方法

1. Controller层

```java
package com.imooc.course.controller;

import com.imooc.course.entity.CoursePrice;
import com.imooc.course.service.CoursePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoursePriceController {

    @Autowired
    private CoursePriceService coursePriceService;
    @GetMapping("/price")
    public Float getCoursePrice(Integer courseId) {
        final CoursePrice coursePrice = coursePriceService.getCoursePrice(courseId);
        return coursePrice.getPrice();
    }
}
```

2. Service层

```java
package com.imooc.course.service;

import com.imooc.course.entity.CoursePrice;

/**
 * 课程价格服务
 */
public interface CoursePriceService {
    public CoursePrice getCoursePrice(Integer courseId);
}
```

```java
package com.imooc.course.service.impl;

import com.imooc.course.dao.CoursePriceMapper;
import com.imooc.course.entity.CoursePrice;
import com.imooc.course.service.CoursePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoursePriceServiceImpl implements CoursePriceService {

    @Autowired
    private CoursePriceMapper coursePriceMapper;

    @Override
    public CoursePrice getCoursePrice(Integer courseId) {
        return coursePriceMapper.findCoursePrice(courseId);
    }
}
```

3. entity层

```java
package com.imooc.course.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CoursePrice {
    Integer id;
    Integer courseId;
    Float price;
}
```

4. dao层

```java
package com.imooc.course.dao;

import com.imooc.course.entity.CoursePrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CoursePriceMapper {

    @Select("select * from course_price where course_id = #{course_id}")
    public CoursePrice findCoursePrice(@Param("course_id") Integer courseId);
}
```

5. 运行

浏览器中输入查询

`http://localhost:8082/price?courseId=409`

# 服务注册Eureka

多个服务之间建立联系的前提, 服务注册

## Eureka

### 什么是Eureka

Netflix核心子模块, 用于定位服务. 用于服务的注册与发现, 使用**服务的标识符**来访问服务, 不需要每次都要修改服务的配置文件. 系统中其他微服务可以使用Eureka-client连接到Eureka-server, Eureka会帮助来维护各个服务的实时信息. 新模块也可以通过Eureka-server直接找到各个组件服务的地址.

例子: 

* 114: 各个服务提供者登记注册在114, 群众(服务调用者)要使用的时候去询问114. 同时如果店铺关门或者长期不响应, 114会返回关门 -> 心跳机制, 保证信息可靠. 
* 公司物业. 维护办公楼中各个公司的信息. 

### 为什么需要服务注册与发现

移除注册中心, 也可以运行, 但是很繁琐. 如果自己来调用:

* IP变化. 如果A服务提供的是http地址, B服务可以在自己服务配置文件中写死, 然后通过http-client调用对方服务. 但是ip和端口号都有可能变化. 

  * 难以维护. A服务地址变化(常见场景, 机器扩容, 业务变动, 服务器到期等). B服务也要跟着变化. 相当于外部依赖变化, 导致自己的代码也需要做代码变更和重新发布. 成本高

  * 影响面. A服务被许多服务调用. 牵一发而动全身, A服务变化, 那么其他的服务都会不可用

所以手动静态配置很繁琐.

---

改进的思路和方案:

服务的Provider的增减变化应该让Consumer及时知晓, 这种知晓不是代码上的改变, 而是尽量无缝衔接. 即使provider变化频繁, consumer也不需要更改. -> 使用服务注册中心

provider将自己的服务地址登记到服务注册中心, consumer不直接调用provider(的ip和端口), 而是先去注册中心查询到一个地址再去调用 -> 无需人工维护结点; 解决多结点的负载均衡(多结点注册上去, 对于consumer有选择地调用)

### Eureka架构

Eureka Server和Eureka Client

<img src="img/spring-cloud-course/image-20220109143007217.png" alt="image-20220109143007217" style="zoom:67%;" />

Eureka Server： 服务注册中心. 独立模块, 不要混在业务模块中. 

Service Provider: 服务提供者. 启动后找到Eureka Server注册中心地址, 将自己的内容注册上去. 最开始的时候注册, 后续也会更新.

Service Consumer: 服务消费者. 启动后调用某些服务. 首先去Eureka Server拿到Registry注册表, 通过注册表拿到最新的服务信息. 再Remote Call远程调用Provider.

---

升级为集群:

<img src="img/spring-cloud-course/image-20220109143317285.png" alt="image-20220109143317285" style="zoom: 50%;" />

3个Eureka Server都是服务注册中心. Eureka Server之间共享信息.

Application Service: 服务提供者. 可以在不同的Eureka Server上注册服务. 

Application Client: 服务调用者. 找到任意一个Eureka Server结点即可, 就可以找到整个服务信息. 

---

总结:

Service Provider: 服务提供者. 

* 启动后找到Eureka Server注册中心地址, 将自己的内容注册上去. 
* 负责续约, 定期发送心跳, 告诉注册中心自己的存活.
* 负责下线. 实例正常关闭, 机器缩减等要通知Eureka Server下线

Service Consumer: 服务消费者

* 获取服务. 通过请求得到Eureka Server注册中心, 拿到服务清单
* 进行调用. 根据清单找到需要的信息进行调用.

Eureka Server: 服务注册中心

* 负责维护. 有服务注册需要记录.
* 失效剔除. 每隔一段时间(默认60s)查看哪些服务没有续约, 就剔除. 因为服务提供者突然挂掉, 或者没有能力通知注册中心下线

## Eureka-Server

引入依赖 -> 配置文件 -> 启动注解(整个spring boot就有了Eureka Server的能力)



1. 在spring-cloud-course(根项目)下新建module为eureka-server, maven项目. 与业务代码平级

![image-20220109144241807](img/spring-cloud-course/image-20220109144241807.png)

![image-20220109144456138](img/spring-cloud-course/image-20220109144456138.png)

2. eureka-server修改pom文件

eureka-server.pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-cloud-course</artifactId>
        <groupId>com.example</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>eureka-server</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

eureka-server中引入springcloud eureka依赖的时候, 对于Spring Cloud的版本信息, 不要在每一个模块中维护, 而是统一在最外层pom文件中声明信息. 统一整个项目的springcloud版本.

spring-cloud-course中(根目录pom)

```xml
<!-- 表示Spring Cloud版本 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Greenwich.SR5</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

3. 增加配置文件

配置name

配置端口

配置eureka

```properties
spring.application.name=eureka-server

server.port=8000

# eureka config
# hostname
eureka.instance.hostname=localhost

# eureka client

# fetch-registry: 获取注册表. 不需要同步其他节点数据. 默认true
eureka.client.fetch-registry=false

# 是否将自己注册到Eureka Server(自己是否是eureka client), 默认是true。
# 这里就是server, 没有对外暴露http服务, 其他模块不会调用这里的服务
eureka.client.register-with-eureka=false

# 服务提供的地址
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/
```

4. 声明springboot和EurekaServer启动类

```java
package com.imooc.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

5. 启动

启动, 访问`http://localhost:8000/`

![image-20220109153229849](img/spring-cloud-course/image-20220109153229849.png)

## Eureka Client改造















# 服务调用Feign



















# Ribbon负载均衡



















# Hystrix断路器



















# 网关Zuul



















# 测试

通过网关与不通过网关











