# 多模块 Spring Boot 项目脚手架

一个基于 Spring Boot 3 的多模块后端项目，分层清晰、职责明确，包含通用、领域、基础设施、业务服务、Web 接入以及应用启动模块。支持多环境配置（dev/test/prod），整合 MyBatis-Plus、Druid、Redis、Knife4j/SpringDoc、Actuator/Prometheus 等。

## 目录结构

```
shoan-boot-template
├── pom.xml                        # 根 POM，聚合与依赖版本管理
├── shoan-boot-common             # 通用模块（工具类/常量/通用配置等）
├── shoan-boot-domain             # 领域模块（实体/值对象/领域服务）
├── shoan-boot-infrastructure     # 基础设施模块（数据访问/MyBatis-Plus/第三方集成）
├── shoan-boot-service            # 业务服务模块（业务逻辑编排）
├── shoan-boot-web                # Web 接入层（Controller/Interceptor/Exception/Swagger配置）
└── shoan-boot-application        # 应用启动模块（唯一入口，打包为可执行Jar）
```
## 基础功能
1. 小程序用户登录
## 模块职责

- shoan-boot-application
  - 唯一启动入口；打包为可执行 JAR；聚合 Web/Service/Infrastructure 等模块。
  - 主类：`cn.shoanadmin.application.ShoanAdminApplication`
  - 扫描：`@SpringBootApplication(scanBasePackages = "cn.shoanadmin")`，`@MapperScan("cn.shoanadmin.infrastructure.mapper")`
  - 配置文件：`src/main/resources` 下的 `application.yml`、`application-dev.yml`、`application-prod.yml`、`application-test.yml`

- shoan-boot-web
  - 对外 HTTP 接口（Controller）、拦截器（Interceptor）、WebMvcConfigurer、异常处理、Swagger/Knife4j 文档。
  - 说明：拦截器建议只保留此模块版本，避免与 application 模块重复导致 Bean 冲突。

- shoan-boot-service
  - 业务逻辑编排，衔接领域与基础设施。

- shoan-boot-infrastructure
  - 数据访问、MyBatis-Plus 与 MyBatis 整合、数据源与连接池（Druid）、第三方系统访问。
  - Mapper XML 建议放置于：`src/main/resources/mapper`，并配合 `mybatis-plus.mapper-locations=classpath:/mapper/*.xml`。

- shoan-boot-domain
  - 领域模型与规则（实体、值对象、领域服务、仓储接口）。

- shoan-boot-common
  - 通用工具/常量/异常等。建议保持轻量，避免引入过多 Starter 造成隐式传递依赖。

## 依赖与版本管理

- 根 POM 继承：`spring-boot-starter-parent:3.3.5`
- 当前 `dependencyManagement` 中将 `boot.version` 设为 `3.2.1`（与父 POM 不一致）。
- 推荐统一：
  - 方案 A（推荐）：删除 Spring Boot Starters 的显式版本声明，完全由父 POM 3.3.5 管理。
  - 方案 B：如果确需指定版本，则将 `boot.version` 统一到 `3.3.5`。
- MySQL 驱动：`mysql:mysql-connector-java` 已迁移至 `com.mysql:mysql-connector-j`。建议更新坐标。

## 配置与环境

- 主配置：`shoan-boot-application/src/main/resources/application.yml`
  - `spring.profiles.active: dev`（默认激活 dev）
  - 数据源（Druid + MySQL）、Redis、MyBatis-Plus、日志、Actuator/Prometheus、Knife4j 等。
- 环境扩展：
  - `application-dev.yml`：开发环境差异（端口/日志级别/Redis数据库/Knife4j等）
  - `application-prod.yml`：生产环境（日志到文件、禁用 Knife4j、指标）
  - `application-test.yml`：测试环境（示例使用 H2 内存数据库）

## 快速开始

### 前置要求
- JDK 21
- Maven 3.9+
- MySQL 与 Redis（dev 环境默认指向本机：`localhost:3306`、`localhost:6379`）

### 构建
```
mvn -DskipTests clean package
```