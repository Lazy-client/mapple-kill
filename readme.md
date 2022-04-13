# 三湘秒杀

![](https://img.shields.io/badge/building-passing-green.svg)![GitHub](https://img.shields.io/badge/license-MIT-yellow.svg)![jdk](https://img.shields.io/static/v1?label=oraclejdk&message=8&color=blue)

项目致力于打造一个分布式架构秒杀平台，采用现阶段流行技术来实现，采用前后端分离编写。



## 项目介绍

包括前台系统以及后台管理系统，基于 SpringCloud、SpringCloud Alibaba、MyBatis Plus实现。前台商城系统包括：用户登录、注册、银行产品搜索、产品详情、订单、秒杀活动等模块。后台管理系统包括：系统管理、产品系统、库存系统、订单系统、用户系统、等模块。




## 项目演示（待续）

### 前台商品系统

#### 首页

#### 商品检索

#### 认证

#### 产品详情

### 后台管理系统

#### 登录

![](https://i.loli.net/2021/02/18/6KVEbFZMrgnUet3.png)

#### 产品系统

**产品管理**

**活动管理**

**库存管理**




## 组织结构

```
mapple-kill
├── mapple-common -- 工具类及通用代码
├── mapple-admin -- 后台管理
├── renren-generator -- 项目的代码生成器
├── mapple-coupon -- 产品，活动服务
├── mapple-gateway -- 统一配置网关
├── mapple-order -- 订单服务
├── mapple-search -- 检索服务
├── mapple-seckill -- 秒杀服务
└── mapple-xxx -- (待续)
```

## 技术选型

### 后端技术

|         技术         |       说明       |                      官网                       |
|:------------------:| :--------------: | :---------------------------------------------: |
|     SpringBoot     |   容器+MVC框架   |     https://spring.io/projects/spring-boot      |
|    SpringCloud     |    微服务架构    |     https://spring.io/projects/spring-cloud     |
| SpringCloudAlibaba |    一系列组件    | https://spring.io/projects/spring-cloud-alibaba |
|    MyBatis-Plus    |     ORM框架      |             https://mp.baomidou.com             |
|  renren-generator  | 项目的代码生成器 |   https://gitee.com/renrenio/renren-generator   |
|      RocketMQ      |     消息队列     |            https://rocketmq.apache.org/             |
|      Redisson      |     分布式锁     |      https://github.com/redisson/redisson       |
|       Docker       |   应用容器引擎   |             https://www.docker.com              |
|        OSS         |    对象云存储    |  https://github.com/aliyun/aliyun-oss-java-sdk  |

### 前端技术

|  技术   |    说明    |           官网           |
| :-----: | :--------: | :----------------------: |
|   Vue   |  前端框架  |    https://vuejs.org     |
| Element | 前端UI框架 | https://element.eleme.io |
| node.js | 服务端的js |  https://nodejs.org/en   |

## 架构图（待续）

### 系统架构图

### 业务架构图

## 环境搭建

### 开发工具

|     工具      |        说明         |                      官网                       |
| :-----------: | :-----------------: | :---------------------------------------------: |
|     IDEA      |    开发Java程序     |     https://www.jetbrains.com/idea/download     |
| RedisDesktop  | redis客户端连接工具 |        https://redisdesktop.com/download        |
|  SwitchHosts  |    本地host管理     |       https://oldj.github.io/SwitchHosts        |
|    X-shell    |  Linux远程连接工具  | http://www.netsarang.com/download/software.html |
|    Navicat    |   数据库连接工具    |       http://www.formysql.com/xiazai.html       |
| PowerDesigner |   数据库设计工具    |             http://powerdesigner.de             |
|    Postman    |   API接口调试工具   |             https://www.postman.com             |
|    Jmeter     |    性能压测工具     |            https://jmeter.apache.org            |
|    Typora     |   Markdown编辑器    |                https://typora.io                |

### 开发环境

|    工具     |  版本号  |                             下载                             |
|:---------:|:-----:| :----------------------------------------------------------: |
|    JDK    |  1.8  | https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html |
|   Mysql   |  5.7  |                    https://www.mysql.com                     |
|   Redis   | Redis |                  https://redis.io/download                   |
| RocketMQ  | 4.X.X |            https://rocketmq.apache.org/             |
| OpenResty | 1.1.6 |              https://openresty.com.cn/cn/               |



### 部署待续

- 修改Linux中Nginx的配置文件

```shell

#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    limit_req_zone $binary_remote_addr zone=req_one:20m rate=15r/s;
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

	upstream mapple-gateway {
                server localhost:90 weight=1;
                server localhost:88 weight=1;
                server localhost:89 weight=1;
        }
    # HTTPS server
    #
    server {
        listen       8888 ssl;
        server_name  lazy.sicheng.store;

        ssl_certificate      cert/7598388_lazy.sicheng.store.pem;
        ssl_certificate_key  cert/7598388_lazy.sicheng.store.key;
        #ssl_session_cache    shared:SSL:1m;
        ssl_session_timeout  5m;
        ssl_protocols TLSv1.1 TLSv1.2 TLSv1.3;
        ssl_ciphers  ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4; #HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers  on;
        #
        #ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

	location /mapple {
           alias  /sxapp/mapple/maple-kill-vue/dist;
           index  index.html;
        }
	
	location /mapple-kill {
           alias /sxapp/mapple/kill-mobile/dist;
           index  index.html;
        }




 	#location / {
        #    proxy_pass      http://localhost:8080/;
        #    proxy_redirect  off;
        #    proxy_set_header Host $host;
        #    proxy_set_header X-Real-IP $remote_addr;
        #    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        # }

	location /api/ {
		#limit_req zone=req_one burst=10 nodelay;
                
		proxy_set_header Host $host;
		proxy_set_header X-Real-IP $remote_addr;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		set $business "mapple-kill";
		access_by_lua_file /usr/local/openresty/nginx/conf/access.lua;
		proxy_pass http://mapple-gateway;
		#content_by_lua_file /usr/local/openresty/nginx/conf/access.lua;
	
	}


        #    root   html;
        #    index  index.html index.htm;
        #}

	error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        } 
    }
}
```





