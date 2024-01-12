# 问卷调查系统

## 前言

本问卷调查系统采用的是 WEB 应用程序开发中最受欢迎的 Spring Boot 框架，使用占用空间小但功能齐全的 MySQL 数据库进行数据的存储操作，前端开发使用了 Vue3框架，其具有极强的可移植性。该问卷调查系统能够解决许多传统手工操作的难题，比如数据查询耽误时间长，数据管理步骤繁琐等问题。



## 项目演示

演示地址：不一定开放。。http://47.115.219.222/

放几张截图吧

![image-20240112164741575](https://hantou-picbed.oss-cn-hangzhou.aliyuncs.com/img/202401121647774.png)

![image-20240112164805208](https://hantou-picbed.oss-cn-hangzhou.aliyuncs.com/img/202401121648290.png)

![image-20240112164819405](https://hantou-picbed.oss-cn-hangzhou.aliyuncs.com/img/202401121648483.png)

![image-20240112164900028](https://hantou-picbed.oss-cn-hangzhou.aliyuncs.com/img/202401121649115.png)



## 项目介绍

### 组织结构

```
my-project
├── my-project-backend -- 后端代码
└── my-project-frontend -- 前端代码
```

### 技术选型

#### 后端技术

| 技术           | 说明             | 官网                                                    |
| -------------- | ---------------- | ------------------------------------------------------- |
| SpringBoot     | Web应用开发框架  | https://spring.io/projects/spring-boot                  |
| SpringSecurity | 认证和授权框架   | https://spring.io/projects/spring-security              |
| MyBatis        | ORM框架          | http://www.mybatis.org/mybatis-3/zh/index.html          |
| RabbitMQ       | RabbitMQ         | [ https://www.rabbitmq.com/](https://www.rabbitmq.com/) |
| Redis          | 内存数据存储     | https://redis.io/                                       |
| Nginx          | 静态资源服务器   | https://www.nginx.com/                                  |
| OSS            | 对象存储         | https://github.com/aliyun/aliyun-oss-java-sdk           |
| JWT            | JWT登录支持      | https://github.com/jwtk/jjwt                            |
| Swagger-UI     | API文档生成工具  | https://github.com/swagger-api/swagger-ui               |
| HikariCP       | 数据库连接池     | https://github.com/brettwooldridge/HikariCP             |
| Kumo           | 词云图片生成工具 | https://github.com/kennycason/kumo                      |

#### 前端技术

| 技术       | 说明                             | 官网                                                  |
| ---------- | -------------------------------- | ----------------------------------------------------- |
| Vue        | 前端框架                         | https://vuejs.org/                                    |
| Vue-router | 路由框架                         | https://router.vuejs.org/                             |
| Element    | 前端UI框架                       | [https://element.eleme.io](https://element.eleme.io/) |
| Axios      | 前端HTTP框架                     | https://github.com/axios/axios                        |
| ECharts    | 基于JavaScript的开源可视化图表库 | https://echarts.apache.org/zh/index.html              |
| Pinia      | 前端状态托管                     | https://github.com/vuejs/pinia                        |

### 架构图

#### 系统架构图

![image-20240111162812320](https://hantou-picbed.oss-cn-hangzhou.aliyuncs.com/img/202401111628510.png)

### 数据库设计



## 谈谈使用的技术

### 后端部分

#### 一、JWT

现在主流的登录认证的Token主要是两种，就是JWT和Session方案。

- JWT

  JWT是一种**无状态**的身份验证机制，这里只需要在登录成功后，后端通过一个秘钥来对对应账号生成一个带签名的Token，在这个Token里可以包含过期时间，用户基本信息等，在本系统中，就在Token里加入了用户的id和role信息，这样可以快速获取用户的基本信息进行一个权限的校验判断，不需要再去查数据库了。

  JWT最大的特点就是无状态，基于它无状态的特性，可以在多个不同的服务期间共享校验信息，同时也不需要存储JWT的信息。

  其常用的操作是建立一个黑名单，将提前失效（如登出操作）的账号的Token加入黑名单，这是即使此时的Token仍是正确且未过期的，也会在黑名单中被拦截； 这样有一个缺点，就是你无法掌握现在发放的Token的使用情况，这样可能会造成一些问题，我在下面的内容里会更详细的说明我遇到的问题。

- Session

  Session是一种**有状态**的身份验证机制，在用户登入成功后吗，后端会创建一个会话，相当于创建一个白名单并且保持长连接，用户和服务器都会拿到Token（Cookie），服务器通过用户传来的Cookie，查询对应回话里的信息，来对用户请求进行相应。

  这种方案会占用比较多的服务器资源来保持和维护连接，同时不符合现在分布式的趋势。、

选择 JWT 还是 Session 方案取决于应用程序的需求和场景：

- JWT 适合分布式和无状态的环境，因为它不需要服务器端存储和状态维护。
- Session 适合需要复杂的会话管理和状态跟踪的应用程序，但需要服务器端存储和额外的资源开销。

#### 二、Spring Security 

之前的项目没有好像这样具体的从零用SpringSecurity搭建过，这次还是直接基于SpringBoot3搭建的，网上资料不多，还是遇到了不少困难。
Spring Security 6.x的版本全部采用了lambda表达式的方式进行配置

```java
    //Security过滤器链
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf->conf
                        .requestMatchers("/api/auth/**","/error").permitAll() //设置验证模块的请求通过
                        .requestMatchers("/api/admin/**").hasAnyRole("admin","superAdmin") //RMBR 管理員特权接口
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/file/getFile/image/**").permitAll()//给请求头像资源的请求放行
                        .anyRequest().hasAnyRole("admin","user","superAdmin") //其他任何请求不得通过
                )
                .formLogin(conf->conf //表单登录
                        .loginProcessingUrl("/api/auth/login") //配置登录的访问
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                        )
                .logout(conf->conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf->conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)//有令牌,权限不足
                )
                .csrf(AbstractHttpConfigurer::disable)// 配置csrf(跨域)
                .sessionManagement(conf->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))//设置Session为无状态
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)//RMBR 需要在用户密码过滤器之前,加入我们自定义的过滤器,这样可以减轻用户密码过滤器的负担
                .build();
    }
```

#### 三、RabbitMQ & Redis

本项目中暂时还没有涉及到需要大量消息交互的情况，所以这里的RabbitMQ只用来处理验证码的邮件。

Redis在本项目里用来处理JWT Token的黑名单和限流操作，同时对邮件验证码也进行验证。

#### 四、MyBatisPlus

之前的地铁查询的项目也用了Mybatis，不过不是Mybatis-Plus，用下来Plus方便多了，许多基础查询都已经设置好了，可以方便的进行查询，这极大地提升了查询的效率。不过涉及到多表间的复杂查询，还是需要使用传统的Mapper进行配置。

### 前端部分

#### 一、Vue3

上学期的地铁线路查询项目中使用了Vue2，之前是看中了Vue项目的多平台，可移植性（万物基于Chrome）来进行开发的，虽然现在也没做跨平台，不过这个问卷系统应该是准备拿来当毕业设计完善的，所以多平台后续也会进行完善的，这次应该能用上多平台的特性了。

老实说现在对Vue还是一知半解的，只是大概的知道一个基础的页面于要怎么设计，怎么连接，Router怎么写等。感觉自己现在写前端页面没有什么标准，就是能实现就行。。

#### 二、Pinia

在设计前端页面时，考虑到具体的业务页面是镶嵌在一个框架里的，框架顶部信息栏需要展示账号的基本信息，同时业务里业务要当前登录账号的基本信息，所以就使用了Pinia来存储查询到的账号信息，这样在使用时就实现了信息的共享，不用单独的再去查询，同时当数据被修改时，也能统一做出相应，还是很好用的。

#### 三、ECharts

这个项目当做数据库课设做的时候是没有做可视化的，就是只做了个管理员可以查询其他用户所填写的问卷信息的逻辑。后续考虑到这里应该要让问卷发布者能更直观的看见其收集的结果，所以在这里添加了可视化功能。

这里使用了Echarts，这玩意看demo功能是真的很强，不过我没有系统的学过前端，它的文档大多数还是vue2的标准，在我的这个项目上不太能跑起来，所以就只用了个简图做了个简单查询。再次吐槽前端，传给Echarts的数据格式要求真的很随便，同一组数据有好多好多不同的样式可以被他解析，你这该说是强大还是繁琐呢。。



## 开发中遇到的问题

### 一、部署到云服务器

想着做都做了，就部署到阿里云上吧，正好这学期白嫖了半年多，想着应该蛮简单，就直接开干。

#### 原生？ 宝塔！

aliyun，启动！直接去下了CentOS7.9，然后就是一顿安装，最后装的乱七八糟，实现是能基本实现了，可是还是难以调试；索性把系统重置了装一个宝塔，部署顺畅了很多，就这样还是又出了很多幺蛾子。

#### Maven 各种报错 JDK版本，POM文件异常等问题

我寻思我本地跑的蛮好的，一打包上云就嘎嘎报错，JDK版本都是小问题，一个IDEA里好几个地方都改改，改统一了就行。这POM的设置的排查就费老鼻子劲了，云上启动报错，说找不到Jar包的主类。？？我不是在POM里配置主类了吗，我到处查资料都查不到，也找不到相关案例，最后是一个一个比着删删出来的，是最后打包的时候，多选了一个东西。

#### 端口的开放与关闭等安全问题

第一次上云，忘了去开端口，宝塔和阿里云上的端口都要打开，好几次要么就是忘了开，要么就是只开了一个，导致调试半天找不到连接的异常，回头发现端口压根没开（我直接一个tcping的连通）

发现有些端口不能随便开，比如说RabbitMQ的端口，非特殊情况就不会对外网暴露，分分钟给你刷爆说是



### 二、词云的处理

这里问卷调查的展示是参考腾讯文档里的收集表，对于文本类型的题目，他们会对答卷中的题目数据生成一个词云模块，就像这样

![image-20240112161838998](https://hantou-picbed.oss-cn-hangzhou.aliyuncs.com/img/202401121618107.png)

我没有找到非常合适的词云前端轮子，因为大多数都是Python写的，然后是特殊的格式，不好直接在我的前端页面进行展示，就去Github上找了个用java写的直接能生成词云图片的kumo词云，效果还不错吧~。在后端生成图片以后，以Base64编码的格式传给前端，然后前端根据传过去的数据类型进行判断，如果题目类型是单选或多选就渲染成饼图，如果是文本题就解析成词云图片。



### 三、JWT失效的实时性问题

这位就比较重量级了，因为现在解决不了。上面提到的JWT有一些固有的问题，这下就给我遇见了。在放寒假后对系统进行第二轮开发的时候，新增了超级管理员，超管可以新增和修改用户的权限role，这引发了一个问题。

新增的还是正常了，但当超管对用户权限进行调整时（尤其是降低权限时），会产生问题。比如说，当前一个权限为admin的管理员用户现在处于登录状态，拿到了一个还在有效期内的Token,超管这时对其权限进行降级至普通用户user，这是数据库内虽然是该用户的权限已经为user了，但他的Token内解析出的role还是admin，他仍然可以访问后端的特权接口进行操作，这显然会造成安全性异常。

首先想到了几种解决办法

- 对当前在有效期内的Token进行注册记录，当对用户权限进行修改时，会拉黑对应用户的Token，这样就实现了安全性

  乍一看是不是很有道理？不过仔细一想，我们用的不是Jwt吗，Jwt不是无状态的吗，你把Token都注册了，为啥不去用Session？显然这样是不对的

- 对于用户的权限修改操作，单独建一个库，当修改发生时，将被修改者的信息加入一个黑名单，当解析前端Token时，如果Token里的信息命中了黑名单，就拒绝该Token并且拉黑

  这种方法好像同样违背了Jwt设计的初衷

好像这个问题有些无解。。。我能想到的比较简单的办法就是缩短Jwt令牌的有效期，然后因为只有占极少数的超级管理员才有修改权限，所以这里就直接交给人判断谨慎处理了，所以是个没有办法的办法？？我没有想到比较合理的解决办法，所以最好在设计系统的时候就不要给这个权限？

### 感谢
感谢白马讲师的教学视频！白马官网https://www.itbaima.cn/
