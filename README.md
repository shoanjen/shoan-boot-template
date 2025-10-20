# Shoan Admin 多模块 Spring Boot 项目

一个基于 Spring Boot 3 的多模块后端项目，分层清晰、职责明确，包含通用、领域、基础设施、业务服务、Web 接入以及应用启动模块。支持多环境配置（dev/test/prod），整合 MyBatis-Plus、Druid、Redis、Knife4j/SpringDoc、Actuator/Prometheus 等。

## 目录结构

```
shoan-admin
├── pom.xml                        # 根 POM，聚合与依赖版本管理
├── shoan-admin-common             # 通用模块（工具类/常量/通用配置等）
├── shoan-admin-domain             # 领域模块（实体/值对象/领域服务）
├── shoan-admin-infrastructure     # 基础设施模块（数据访问/MyBatis-Plus/第三方集成）
├── shoan-admin-service            # 业务服务模块（业务逻辑编排）
├── shoan-admin-web                # Web 接入层（Controller/Interceptor/Exception/Swagger配置）
└── shoan-admin-application        # 应用启动模块（唯一入口，打包为可执行Jar）
```

## 模块职责

- shoan-admin-application
  - 唯一启动入口；打包为可执行 JAR；聚合 Web/Service/Infrastructure 等模块。
  - 主类：`cn.shoanadmin.application.ShoanAdminApplication`
  - 扫描：`@SpringBootApplication(scanBasePackages = "cn.shoanadmin")`，`@MapperScan("cn.shoanadmin.infrastructure.mapper")`
  - 配置文件：`src/main/resources` 下的 `application.yml`、`application-dev.yml`、`application-prod.yml`、`application-test.yml`

- shoan-admin-web
  - 对外 HTTP 接口（Controller）、拦截器（Interceptor）、WebMvcConfigurer、异常处理、Swagger/Knife4j 文档。
  - 说明：拦截器建议只保留此模块版本，避免与 application 模块重复导致 Bean 冲突。

- shoan-admin-service
  - 业务逻辑编排，衔接领域与基础设施。
  - 提示：存在旧版 `web/WEB-INF/web.xml` 的遗留痕迹（传统 WAR 项目），Spring Boot 不需要，建议删除。

- shoan-admin-infrastructure
  - 数据访问、MyBatis-Plus 与 MyBatis 整合、数据源与连接池（Druid）、第三方系统访问。
  - Mapper XML 建议放置于：`src/main/resources/mapper`，并配合 `mybatis-plus.mapper-locations=classpath:/mapper/*.xml`。

- shoan-admin-domain
  - 领域模型与规则（实体、值对象、领域服务、仓储接口）。

- shoan-admin-common
  - 通用工具/常量/异常等。建议保持轻量，避免引入过多 Starter 造成隐式传递依赖。

## 依赖与版本管理

- 根 POM 继承：`spring-boot-starter-parent:3.3.5`
- 当前 `dependencyManagement` 中将 `boot.version` 设为 `3.2.1`（与父 POM 不一致）。
- 推荐统一：
  - 方案 A（推荐）：删除 Spring Boot Starters 的显式版本声明，完全由父 POM 3.3.5 管理。
  - 方案 B：如果确需指定版本，则将 `boot.version` 统一到 `3.3.5`。
- MySQL 驱动：`mysql:mysql-connector-java` 已迁移至 `com.mysql:mysql-connector-j`。建议更新坐标。

## 配置与环境

- 主配置：`shoan-admin-application/src/main/resources/application.yml`
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

### 运行（开发环境）
- 方式一：打包运行
```
java -jar shoan-admin-application/target/shoan-admin-application-*.jar
```
- 方式二：显式激活 profile
```
java -jar shoan-admin-application/target/shoan-admin-application-*.jar --spring.profiles.active=dev
```
- Knife4j 文档：
```
http://localhost:8080/doc.html
```

### 运行（IDE）
- 选择 `ShoanAdminApplication` 主类运行。
- 确认 Working Directory 指向根项目或 application 模块。
- 如需显式激活：在 Program arguments 添加 `--spring.profiles.active=dev`。

## 常见问题与排查

1) Failed to configure a DataSource（提示未指定 url 或未激活 profile）
- 原因：启动了错误模块/打包类型错误导致资源未入 classpath；或未显式激活 profile；或 IDE 工作目录错误。
- 解决：仅使用 `shoan-admin-application` 启动；确保 packaging=jar；使用 `--spring.profiles.active=dev`；检查 `src/main/resources` 是否随应用打包。

2) Bean 注入失败（如 WechatUserService 无法注入）
- 原因：组件扫描范围不足或存在重复拦截器导致冲突。
- 解决：主类使用 `scanBasePackages="cn.shoanadmin"`；只保留 web 模块的 `AuthInterceptor`；在 `WebConfig` 中注册拦截器。

3) MyBatis-Plus Mapper 找不到
- 原因：`mapper-locations` 与实际 XML 路径不一致。
- 解决：确保 XML 位于 `shoan-admin-infrastructure/src/main/resources/mapper`，并与 `@MapperScan` 包一致。

## 运维与监控
- Actuator：`/actuator`（health/info/metrics/prometheus）
- 日志：控制台与文件（见 `application.yml` 的 `logging.file` 配置）
- Prometheus：开启指标导出以集成监控系统。

## 设计原则（简述）
- 分层清晰：Web → Service → Domain → Infrastructure → Common
- 领域驱动：尽量在 domain 层保持纯净，减少框架耦合。
- 配置集中：统一在应用模块的 resources 中管理多环境配置。

## 后续优化建议
- 统一 Spring Boot 版本管理至 3.3.5（或完全由父 POM托管）。
- 更新 MySQL 驱动坐标至 `com.mysql:mysql-connector-j`。
- 清理 `shoan-admin-service/web/WEB-INF/web.xml` 等 WAR 遗留物。
- 精简 `shoan-admin-common` 依赖，减少隐式 Starter 传递。
- 合并/移除重复拦截器，统一在 web 模块注册。

---

如需我直接为你应用上述优化（版本统一、驱动更新、拦截器清理、README细化等），请告诉我要优先处理的项。我可以在本仓库中直接修改并提交。