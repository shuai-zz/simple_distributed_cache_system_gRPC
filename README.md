# simple_distributed_cache_system_gRPC

## 目标

完成一个简易的分布式缓存系统

## 基本要求
1. Cache数据以Key-value形式存储在缓存系统节点内存中（不需要持久化）；
2. Cache数据以既定策略（round-robin或hash均可，不做限定，**本项目使用hash**）分布在不同节点（不考虑副本存储）；
3. 服务至少启动3个节点，不考虑节点动态变化；
    1. 所有节点均提供HTTP访问入口；
    2. 客户端读写访问可从任意节点接入，每个请求只支持一个key存取；
    3. 若数据所在存储服务器与接入服务器不同，接入服务器通过内部RPC(**本项目使用gRPC作为内部RPC**)从目标存储服务器获取数据，再返回至客户端。

## HTTP API协议

1. Content-type: application/json; charset=utf-8
2. 写入/更新缓存：POST /。使用HTTP POST方法，请求发送至根路径，请求体为JSON格式的KV内容，示例如下：

    ```bash
    curl -X POST -H "Content-type: application/json" http://server1/ -d '{"my_name":"电子科技大学@2025"}'
    curl -X POST -H "Content-type: application/json" http://server2/ -d '{"tasks":["task1","task2"]}'
    curl -X POST -H "Content-type: application/json" http://server3/ -d '{"age":123}'
    ```

3. 读取缓存 GET /{key}。使用HTTP GET方法，key直接拼接在根路径之后。为简化程序，对key格式不做要求。
    1. 正常：返回HTTP 200，body为JSON格式的KV结果；
    2. 错误：返回HTTP 404，body为空。
    ```bash
   curl http://server2/myname
   {"my_name": "电子科技大学@2025"}
   curl http://server1/tasks
   {"tasks": ["task 1", "task 2", "task 3"]}
   curl http://server1/notexistkey
   # 404, not found
   ```

4. 删除缓存 DELETE /{key}。永远返回HTTP 200，body为删除的数量。
    ```bash
    curl -XDELETE http://server3/myname
    1
    curl http://server1/myname
    # 404, not found
    curl -XDELETE http://server3/myname
    0
    ```

## 测试

1. 不限语言，提交程序源代码（仅限源代码）
2. 程序必须基于docker打包，并通过docker compose启动运行（每个cache server为一个docker实例）；
3. Dockerfile：保证执行docker build可构建成功。统一使用ubuntu:20.04为基础镜像。

    ```Dockerfile
   FROM ubuntu:20.04
   # add your own codes
   # start your application, one docker one cache server
   ENTRYPOINT []
   ```

4. compose.yaml：能直接启动不少于规定数量的cache server。每个server将内部HTTP服务端口映射至Host，外部端口从9527递增，即若启动3个server，则通过 http://127.0.0.1:9527，http://127.0.0.1:9528，http://127.0.0.1:9529可分别访问3个cache server。
5. 测试代码仓库： https://github.com/ruini-classes/sdcs-testsuit

## 项目简介

sdcs-grpc/  
├── pom.xml                     # Maven项目配置文件，定义项目依赖和构建配置  
├── Dockerfile                  # Docker镜像构建文件，用于构建缓存服务的容器镜像  
├── docker-compose.yml          # Docker Compose编排文件，用于多节点缓存服务的容器编排  
├── src/    
│   ├── main/  
│   │   ├── java/  
│   │   │   └── com/example/sdcs/  
│   │   │       ├── SdcsApplication.java          # Spring Boot主应用启动类  
│   │   │       │  
│   │   │       ├── controller/  
│   │   │       │   └── CacheController.java      # HTTP请求控制器，处理缓存的增删查API  
│   │   │       │  
│   │   │       ├── service/  
│   │   │       │   ├── CacheService.java         # 提供本地缓存以及增删改功能  
│   │   │       │   ├── NodeRouter.java           # 节点路由服务，根据key确定数据所在的节点  
│   │   │       │   └── GrpcClientManager.java    # gRPC客户端管理器，管理与其他节点的gRPC连接  
│   │   │       │  
│   │   │       ├── grpc/  
│   │   │       │   ├── GrpcCacheServer.java      # gRPC服务端实现，处理其他节点的缓存请求  
│   │   │       │   ├── GrpcShutdownHook.java     # gRPC服务关闭钩子，用于清理资源
│   │   │       │   └── GrpcClients.java          # gRPC客户端集合，维护到各节点的gRPC客户端连接  
│   │   │       │  
│   │   │       ├── config/  
│   │   │       │   └── NodeConfig.java           # 节点配置类  
│   │   │  
│   │   ├── proto/  
│   │   │   └── cache.proto                       # Protocol Buffers定义文件，定义gRPC服务和消息格式  
│   │   │  
│   │   └── resources/  
│   │       ├── application.yaml                  # Spring Boot应用配置文件，配置服务端口和节点信息
│   │  
│   └── test/  
│       └── test.sh                               # 自动化测试脚本，用于验证分布式缓存系统的功能

## 运行本项目
注意事项：本项目使用jdk21实现，测试脚本要求bash4及以上
1. 克隆代码并进入项目目录
    ``` bash
    git clone https://github.com/shuai-zz/simple_distributed_cache_system_gRPC.git 
    cd simple_distributed_cache_system_gRPC
    ```
2. maven构建项目
    ```bash
    mvn clean package -DskipTests
    ```
    - 构建成功后在target/目录下生成sdcs-0.0.1-SNAPSHOT.jar文件
3. 构建并运行docker容器
    ```bash
    docker compose up --build
    ```
4. 运行测试脚本
    ```bash
    bash src/test/test.sh 3
    ```
