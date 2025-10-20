# ShoanAdmin 项目需求实现方案
文档版本：v1.1  
编写日期：2025-10-20  
依据文件：ShoanAdmin-PRD.md、.trae/rules/project_rules.md、DATABASE_DESIGN.md、现有认证代码（MiniAppAuthController、AuthInterceptor、TokenUtil）

## 1. 概述与目标
- 目标：在现有多模块后端架构基础上，落地 RBAC（用户、角色、权限、菜单）核心能力与小程序认证登录；日志与 AI 生成代码模块暂缓。  
- 成功标准：接口与页面可用、权限严格一致、数据可追溯（审计字段）、分页与校验完善、与前端对齐的 API 规范统一。

## 2. 范围（当前迭代：M2）
- 实施模块：用户管理、角色管理、权限管理、菜单管理、分配关系（用户→角色、角色→权限、菜单→角色）；小程序登录认证与会话维护。  
- 暂缓模块：日志管理、AI 生成代码、在线统计（仅保留占位，后续再设计）。

## 3. 技术与架构
- 后端：Spring Boot + MyBatis-Plus + Lombok；密码哈希：BCrypt；分页：MyBatis-Plus 分页插件。  
- 认证方案（不使用 JWT）：采用现有自定义 Token（Base64 编码的键值对字符串）+ 拦截器认证。
  - 关键组件：
    - TokenUtil（生成与解析 token）：claims 包含 userId、deviceType、expires_in（建议默认 7 天）。
    - MiniAppAuthController（登录/退出/用户信息）：小程序登录入口 `/api/v1/auth/miniapp/login`，用户信息 `/api/v1/auth/user/info`，退出 `/api/v1/auth/logout`。
    - AuthInterceptor（拦截器）：读取请求头 `en-bit-token`，解析并校验用户身份，将用户注入 UserContext。  
- 分层约定：Controller（API）→ Service（事务与业务规则）→ Mapper（MyBatis-Plus）。  
- 公用规范：DTO 命名 xxxReq/xxxRes；审计字段 create_time/update_time/create_by/update_by 使用 long；逻辑删除 deleted。

## 4. 数据模型与表（对齐 DATABASE_DESIGN.md）
- 主表：sys_user、sys_role、sys_permission、sys_menu。  
- 映射表：sys_user_role、sys_role_permission、sys_menu_role。  
- 字段字典与 DDL 草案已在 DATABASE_DESIGN.md；执行前需确认。  
- 唯一性策略：默认仅主键唯一；role.key、permission.key 建议应用层强校验，是否加唯一索引待评估。

## 5. API 设计（认证与 RBAC）
- 认证（统一采用 `/api/v1/auth` 前缀）：
  - POST `/api/v1/auth/miniapp/login`（小程序登录，返回自定义 token 与用户信息）
   - GET  `/api/v1/auth/user/info`（从 UserContext 获取当前用户信息）
   - POST `/api/v1/auth/logout`（清理用户上下文）
  - Header 约定：`en-bit-token: <token>`（由前端在每次请求中携带）
- RBAC（建议采用统一前缀 `/api/v1`，如需与现有继续对齐可暂保留已有路径并在后续统一）：
  - 用户：GET/POST/PUT/DELETE `/api/v1/users`，POST `/api/v1/users/{id}/roles`
  - 角色：GET/POST/PUT/DELETE `/api/v1/roles`，POST `/api/v1/roles/{id}/permissions`
  - 权限：GET/POST/PUT/DELETE `/api/v1/permissions`
  - 菜单：GET/POST/PUT/DELETE `/api/v1/menus`，POST `/api/v1/menus/{id}/roles`
- 通用：分页参数 page/size；统一响应 `{code,message,data}`；异常处理统一封装。

## 6. 业务规则与校验
- 用户：用户名唯一（应用层校验），邮箱/手机号格式校验；头像为 URL；支持性别枚举；密码哈希存储。  
- 角色：name（可重复）、key（建议唯一，应用层校验）；审计字段完整；逻辑删除保留。  
- 权限：key 唯一（应用层），type=button/api；描述可选。  
- 菜单：name/icon/path/order/visible；按角色控制显隐。  
- 分配关系：
  - 用户→角色：保存后权限计算即时生效（清理缓存或重新加载）。
  - 角色→权限：更新后接口鉴权基于权限标识生效。
  - 菜单→角色：前端根据用户角色控制路由显隐。  
- 控制器职责：参数校验与组装；服务层：唯一性检查、并发控制、事务提交；Mapper：查询与写入。  
- 使用 Hibernate Validator 注解与服务层二次校验；违反规则返回标准错误码与 message。

## 7. 安全与认证细则（自定义 Token）
- Token 格式：Base64(encodedString)，其中 encodedString 为 `key:value` 的拼接（目前使用 MapUtil/ComStrConstant 约定分隔符）。
- Claims：
  - `userId`：用户唯一标识；
  - `deviceType`：设备类型（如 miniapp）；
  - `expires_in`：过期时间戳（毫秒），默认 7 天。  
- 生成：`TokenUtil.generateToken(userId, deviceType)` 在登录成功后生成；返回给前端保存。  
- 传输：前端每次请求在 Header 中携带 `en-bit-token`。  
- 校验：`AuthInterceptor` 解析 token，获取 `userId` 并校验有效性，查询用户后写入 `UserContext`。  
- 安全建议（不改变“非 JWT”的前提下）：
  1) 增加签名：对 encodedString 使用服务端密钥进行 HMAC-SHA256 签名，并将签名附加到 token（例如 `payload.base64 + '.' + signature.base64`），拦截器验证签名，防止伪造与篡改。
  2) 校验过期：拦截器必须校验 `expires_in` 是否过期（当前实现建议补充）。
  3) 单点与撤销（可选）：引入 `tokenId` 并在 Redis 维护白名单/黑名单，实现踢出与登出所有设备。
  4) 最小化信息：仅保存必要 claims；敏感数据不可明文置于 token。  
- CORS：仅允许受信来源（例如 `http://localhost:5173`）。

## 8. 分页与查询性能
- 已配置 MyBatis-Plus 分页插件（MybatisPlusConfig）。所有列表查询支持分页与安全上限（size ≤ 100）。
- 默认排序：按 `create_time desc`。

## 9. 前端实现计划（Vue3+TS+Vite+elementplus）
- 布局：左侧导航（首页、用户、角色、权限、菜单）、顶部 Header（头像下拉、退出、个人信息、修改密码）。  
- 页面：
  - 用户列表与表单（新增/编辑/删除/批量删除/分配角色、筛选与分页）。
  - 角色列表与表单（新增/编辑/删除/批量删除/分配权限）。
  - 权限列表与表单（新增/编辑/删除/批量删除）。
  - 菜单配置（新增/编辑/删除/批量删除/分配角色）。  
- 交互规范：新增/编辑使用 Modal；删除二次确认；日志页与 AI 入口暂不实现。  
- 路由与守卫：根据 `/api/v1/auth/user/info` 返回的角色/权限控制路由显隐与按钮禁用。

## 10. 测试与验收
- 单元测试：Service 层权限计算与校验逻辑；密码哈希与校验；分页查询。  
- 集成测试：拦截器认证与过期校验；角色/权限变更后生效验证；批量操作正确性。  
- 验收对齐 PRD：
  - 用户/角色/权限/菜单 CRUD 与分配操作完成；
  - 表单校验与错误提示；
  - 分配关系即时生效；
  - 列表分页与选择批量操作可用；
  - 小程序登录认证闭环（登录→携带 token 调用→获取用户信息→退出）。

## 11. 发布与配置
- 构建：`mvn -DskipTests clean package`；多模块产物打包与版本标注。  
- 环境：dev/stage/prod 配置隔离；数据库连接与密钥以环境变量管理。  
- CORS：允许 `http://localhost:5173`（前端开发地址）。

## 12. 计划与里程碑
- M2（当前）：RBAC 全量打通 + 小程序认证登录。
  - 周期：1-2 周；交付：后端接口与前端页面可用，数据库表落地；
  - 风险：唯一性与并发、权限粒度一致性、token 伪造风险；
  - 对策：服务层二次校验、补充拦截器过期校验与（可选）签名机制、集成测试、灰度发布。
- 后续：M3（日志与在线统计）、M4（AI 生成代码）、M5（加固与上线）。

## 13. 代码一致性与 Linter 修复（建议）
- 现状：Linter 未正确识别 Lombok 的 @Slf4j/@UtilityClass，导致静态调用/日志不可用；MapUtil/JsonUtil 方法为非静态导致静态上下文调用异常。  
- 建议修复：
  1) TokenUtil 去除 @UtilityClass 或显式将方法与常量改为 `static`；若保留 Lombok，仍显式 `static` 关键字以兼容 linter。
  2) 日志：在 TokenUtil 中显式定义 `private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TokenUtil.class);` 并用 `LOG.debug(...)` 代替 `log`。
  3) MapUtil/JsonUtil 调用：若方法为实例方法，则改为 `new MapUtil().stringToMapSeparated(...)` 或将通用方法改为静态；优先减少外部依赖，TokenUtil 内部实现简单解析/拼接以降低耦合。
  4) MiniAppAuthInterceptor/MiniAppAuthController：统一使用 TokenUtil 的静态方法；补充过期校验与异常处理。  
- 此项为开发任务，不影响当前“非 JWT”的认证方案定位。

## 14. 待确认事项
1) 认证路径已统一为 `/api/v1/auth/...`；RBAC 模块采用 `/api/v1/...`。如需与历史接口兼容，可在 WebConfig 中暂保留旧路径的排除规则，并在后续版本完成完全切换。  
2) role.key 与 permission.key 是否在数据库层做唯一索引（或仅应用层校验）。  
3) MySQL 排序规则：`utf8mb4_unicode_ci` 或 MySQL8 的 `utf8mb4_0900_ai_ci`。  
4) sys_user 是否增加 `status/enabled` 字段以支持禁用用户。  
5) 自定义 token 是否增加签名（推荐）与撤销机制（Redis 白/黑名单），以提升安全性。