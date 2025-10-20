# ShoanAdmin 数据库设计文档

文档版本：v1.4  
最后更新：2025-10-20  
适用范围：ShoanAdmin 管理中台（后端服务与数据库）  
参考：ShoanAdmin-PRD.md、.trae/rules/project_rules.md

---

## 1. 设计原则与约束
- 四要素：所有表均包含 create_time、update_time、create_by、update_by（时间字段以毫秒时间戳，库表类型 BIGINT；代码以 long）。
- 主键策略：统一使用 BIGINT 作为主键（建议雪花ID，与 MyBatis-Plus ASSIGN_ID 兼容）。
- 字符集与排序：统一使用 utf8mb4；排序规则推荐 utf8mb4_0900_ai_ci（如现有库为 utf8mb4_unicode_ci，保持一致）。
- 存储引擎：InnoDB。
- 索引规范：仅创建主键唯一索引；其他业务索引由业务按需选择创建（文档以注释形式给出建议）。
- 逻辑删除：统一字段 deleted TINYINT(1) DEFAULT 0（0 正常，1 删除）。
- 命名规范：snake_case；表名使用 sys_* 前缀（系统/管理域），映射表使用来源_目标组合命名；避免使用保留字（如 type、desc）。
- 外键策略：为简化迁移与提升性能，默认不使用外键约束，关系完整性由业务层保证（必要时在文档中给出唯一/组合唯一建议）。

## 2. 术语与定义
- 用户（User）：系统登录与操作主体，具备零个或多个角色。
- 角色（Role）：权限集合载体，用于控制用户的权限范围。
- 权限（Permission）：具体的操作能力标识，粒度可到按钮/接口。
- 菜单（Menu）：前端导航与路由展示项，可按角色控制显隐与访问。
- 映射关系（Mapping）：用户-角色、角色-权限、菜单-角色的多对多关系。
- 日志（Log）：操作/审计日志，用于追踪关键操作、错误与性能。
- AI 生成日志（AI Generation Log）：对接大模型生成代码的记录。

## 3. 环境与编码
- 数据库：fruit_pieces_favorites（与 application.yml 保持一致）。
- MySQL 版本：建议 8.x。
- 代码映射：MyBatis-Plus（ID 策略、逻辑删除、审计字段）。
- 接口前缀（参考项目规则）：/api/v1/模块名/（示例：/api/v1/user/updateUserInfo）。

## 4. 表清单（总览）
- 主表：
  - sys_user（用户）
  - sys_role（角色）
  - sys_permission（权限）
  - sys_menu（菜单）
- 映射表：
  - sys_user_role（用户-角色）
  - sys_role_permission（角色-权限）
  - sys_menu_role（菜单-角色）
- 审计与运营（暂缓设计，暂无 DDL）：
  - sys_log（系统操作与审计日志）
- AI 模块（暂缓设计，暂无 DDL）：
  - ai_generation_log（AI 生成代码日志）

## 5. 全局命名与字段映射
- 统一字段：create_by、update_by、create_time、update_time、deleted。
- 时间字段：以毫秒时间戳（BIGINT/long）。
- 示例字段映射：
  - created_at/updated_at → 统一为 create_time/update_time。
  - created_by/updated_by → 统一为 create_by/update_by。

---

## 6. 表详细设计

### 6.1 sys_user（用户信息）
用途：存储用户基础信息（头像、用户名、性别、邮箱、电话、密码哈希）及审计字段。  
字段：
- id BIGINT 主键
- name VARCHAR(64) 必填（用户名）
- password VARCHAR(255) 密码哈希（BCrypt/Argon2），严禁明文
- avatar_url VARCHAR(255) 头像地址
- gender TINYINT 性别（0未知，1男，2女）
- email VARCHAR(128) 邮箱
- phone VARCHAR(32) 电话
- create_by BIGINT 创建人
- update_by BIGINT 更新人
- create_time BIGINT 创建时间（毫秒）
- update_time BIGINT 更新时间（毫秒）
- deleted TINYINT(1) 逻辑删除（0正常，1删除）
索引建议：主键；可选 email/phone/create_time。

DDL：
```
CREATE DATABASE IF NOT EXISTS fruit_pieces_favorites CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fruit_pieces_favorites;

CREATE TABLE IF NOT EXISTS sys_user (
  id           BIGINT      NOT NULL COMMENT '主键，雪花ID',
  name         VARCHAR(64) NOT NULL COMMENT '用户名',
  password     VARCHAR(255)         COMMENT '密码哈希（BCrypt/Argon2 等），第三方登录可为空',
  avatar_url   VARCHAR(255)         COMMENT '头像图片地址',
  gender       TINYINT               COMMENT '性别：0未知，1男，2女',
  email        VARCHAR(128)          COMMENT '邮箱',
  phone        VARCHAR(32)           COMMENT '电话',
  create_by    BIGINT                COMMENT '创建人ID',
  update_by    BIGINT                COMMENT '更新人ID',
  create_time  BIGINT      NOT NULL  COMMENT '创建时间（毫秒时间戳，long）',
  update_time  BIGINT      NOT NULL  COMMENT '更新时间（毫秒时间戳，long）',
  deleted      TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='用户信息';
-- 可选索引：email、phone、create_time
```

### 6.2 sys_role（角色）
用途：角色管理（名称与标识），用于 RBAC。
字段：
- id BIGINT 主键
- name VARCHAR(64) 角色名称
- role_key VARCHAR(64) 角色标识（建议唯一）
- description VARCHAR(255) 描述
- status TINYINT(1) 默认1（1启用 0禁用）
- create_by BIGINT 创建人
- update_by BIGINT 更新人
- create_time BIGINT 创建时间
- update_time BIGINT 更新时间
- deleted TINYINT(1) 逻辑删除
索引建议：主键；可选唯一 role_key。

DDL：
```
CREATE TABLE IF NOT EXISTS sys_role (
  id           BIGINT       NOT NULL COMMENT '主键ID',
  name         VARCHAR(64)  NOT NULL COMMENT '角色名称',
  role_key     VARCHAR(64)  NOT NULL COMMENT '角色标识（建议唯一）',
  description  VARCHAR(255)          COMMENT '描述',
  status       TINYINT(1)            DEFAULT 1 COMMENT '状态：1启用 0禁用',
  create_by    BIGINT                COMMENT '创建人',
  update_by    BIGINT                COMMENT '更新人',
  create_time  BIGINT       NOT NULL COMMENT '创建时间（毫秒）',
  update_time  BIGINT       NOT NULL COMMENT '更新时间（毫秒）',
  deleted      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='角色表';
-- 建议唯一：uniq_role_key(role_key)
```

### 6.3 sys_permission（权限）
用途：权限标识（按钮/接口）。
字段：
- id BIGINT 主键
- name VARCHAR(64) 权限名称
- perm_key VARCHAR(128) 权限标识（建议唯一）
- perm_type TINYINT(1) 权限类型：1按钮 2接口
- description VARCHAR(255) 描述
- status TINYINT(1) 默认1（1启用 0禁用）
- create_by BIGINT 创建人
- update_by BIGINT 更新人
- create_time BIGINT 创建时间
- update_time BIGINT 更新时间
- deleted TINYINT(1) 逻辑删除
索引建议：主键；可选唯一 perm_key。

DDL：
```
CREATE TABLE IF NOT EXISTS sys_permission (
  id           BIGINT        NOT NULL COMMENT '主键ID',
  name         VARCHAR(64)   NOT NULL COMMENT '权限名称',
  perm_key     VARCHAR(128)  NOT NULL COMMENT '权限标识（建议唯一）',
  perm_type    TINYINT(1)    NOT NULL COMMENT '权限类型：1按钮 2接口',
  description  VARCHAR(255)           COMMENT '描述',
  status       TINYINT(1)             DEFAULT 1 COMMENT '状态：1启用 0禁用',
  create_by    BIGINT                 COMMENT '创建人',
  update_by    BIGINT                 COMMENT '更新人',
  create_time  BIGINT        NOT NULL COMMENT '创建时间（毫秒）',
  update_time  BIGINT        NOT NULL COMMENT '更新时间（毫秒）',
  deleted      TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='权限表';
-- 建议唯一：uniq_perm_key(perm_key)
```

### 6.4 sys_menu（菜单）
用途：前端菜单与路由控制。
字段：
- id BIGINT 主键
- name VARCHAR(64) 菜单名称
- icon VARCHAR(64) 菜单图标
- path VARCHAR(128) 菜单路径（建议唯一）
- sort_order INT 默认0（越小越靠前）
- visible TINYINT(1) 默认1（1可见 0隐藏）
- parent_id BIGINT 父菜单ID（树）
- description VARCHAR(255) 描述
- create_by BIGINT 创建人
- update_by BIGINT 更新人
- create_time BIGINT 创建时间
- update_time BIGINT 更新时间
- deleted TINYINT(1) 逻辑删除
索引建议：主键；可选唯一 path；可选 parent_id。

DDL：
```
CREATE TABLE IF NOT EXISTS sys_menu (
  id           BIGINT        NOT NULL COMMENT '主键ID',
  name         VARCHAR(64)   NOT NULL COMMENT '菜单名称',
  icon         VARCHAR(64)            COMMENT '菜单图标',
  path         VARCHAR(128)  NOT NULL COMMENT '菜单路径',
  sort_order   INT                   DEFAULT 0 COMMENT '排序（越小越靠前）',
  visible      TINYINT(1)            DEFAULT 1 COMMENT '是否可见：1可见 0隐藏',
  parent_id    BIGINT                 COMMENT '父菜单ID（树结构）',
  description  VARCHAR(255)           COMMENT '描述',
  create_by    BIGINT                 COMMENT '创建人',
  update_by    BIGINT                 COMMENT '更新人',
  create_time  BIGINT        NOT NULL COMMENT '创建时间（毫秒）',
  update_time  BIGINT        NOT NULL COMMENT '更新时间（毫秒）',
  deleted      TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='菜单表';
-- 建议唯一：uniq_menu_path(path)
-- 建议索引：idx_menu_parent(parent_id)
```

### 6.5 sys_user_role（用户-角色映射）
用途：用户与角色多对多关系。
字段：
- id BIGINT 主键
- user_id BIGINT 用户ID
- role_id BIGINT 角色ID
- create_by BIGINT 创建人
- update_by BIGINT 更新人
- create_time BIGINT 创建时间
- update_time BIGINT 更新时间
- deleted TINYINT(1) 逻辑删除
索引建议：主键；可选组合唯一 (user_id, role_id)。

DDL：
```
CREATE TABLE IF NOT EXISTS sys_user_role (
  id          BIGINT      NOT NULL COMMENT '主键ID',
  user_id     BIGINT      NOT NULL COMMENT '用户ID',
  role_id     BIGINT      NOT NULL COMMENT '角色ID',
  create_by   BIGINT               COMMENT '创建人',
  update_by   BIGINT               COMMENT '更新人',
  create_time BIGINT      NOT NULL COMMENT '创建时间（毫秒）',
  update_time BIGINT      NOT NULL COMMENT '更新时间（毫秒）',
  deleted     TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='用户-角色映射';
-- 建议组合唯一：uniq_user_role(user_id, role_id)
```

### 6.6 sys_role_permission（角色-权限映射）
用途：角色与权限多对多关系。
字段：
- id BIGINT 主键
- role_id BIGINT 角色ID
- permission_id BIGINT 权限ID
- create_by BIGINT 创建人
- update_by BIGINT 更新人
- create_time BIGINT 创建时间
- update_time BIGINT 更新时间
- deleted TINYINT(1) 逻辑删除
索引建议：主键；可选组合唯一 (role_id, permission_id)。

DDL：
```
CREATE TABLE IF NOT EXISTS sys_role_permission (
  id            BIGINT      NOT NULL COMMENT '主键ID',
  role_id       BIGINT      NOT NULL COMMENT '角色ID',
  permission_id BIGINT      NOT NULL COMMENT '权限ID',
  create_by     BIGINT               COMMENT '创建人',
  update_by     BIGINT               COMMENT '更新人',
  create_time   BIGINT      NOT NULL COMMENT '创建时间（毫秒）',
  update_time   BIGINT      NOT NULL COMMENT '更新时间（毫秒）',
  deleted       TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='角色-权限映射';
-- 建议组合唯一：uniq_role_perm(role_id, permission_id)
```

### 6.7 sys_menu_role（菜单-角色映射）
用途：菜单按角色控制显隐与访问。
字段：
- id BIGINT 主键
- menu_id BIGINT 菜单ID
- role_id BIGINT 角色ID
- create_by BIGINT 创建人
- update_by BIGINT 更新人
- create_time BIGINT 创建时间
- update_time BIGINT 更新时间
- deleted TINYINT(1) 逻辑删除
索引建议：主键；可选组合唯一 (menu_id, role_id)。

DDL：
```
CREATE TABLE IF NOT EXISTS sys_menu_role (
  id          BIGINT      NOT NULL COMMENT '主键ID',
  menu_id     BIGINT      NOT NULL COMMENT '菜单ID',
  role_id     BIGINT      NOT NULL COMMENT '角色ID',
  create_by   BIGINT               COMMENT '创建人',
  update_by   BIGINT               COMMENT '更新人',
  create_time BIGINT      NOT NULL COMMENT '创建时间（毫秒）',
  update_time BIGINT      NOT NULL COMMENT '更新时间（毫秒）',
  deleted     TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='菜单-角色映射';
-- 建议组合唯一：uniq_menu_role(menu_id, role_id)
```

### 6.8 sys_log（系统操作与审计日志）
状态：暂缓设计与实现。
说明：当前阶段不考虑日志管理模块，后续版本再给出字段字典与 DDL。
- 保留需求摘要：时间、用户、动作、目标、状态、耗时、请求ID、接口路径、摘要等。
- 在落地实现前不进行 SQL 设计与执行。


### 6.9 ai_generation_log（AI 生成代码日志）
状态：暂缓设计与实现。
说明：当前阶段不考虑 AI 生成代码模块，后续版本再给出字段字典与 DDL。
- 保留需求摘要：操作者、任务ID、提示、输出路径、状态、错误信息、审计字段。
- 在落地实现前不进行 SQL 设计与执行。


---

## 7. 执行与发布流程
- 本文档中的 DDL 为“草案/建议”，执行前需研发负责人确认。
- 执行顺序建议：主表（user/role/permission/menu）→ 映射表；日志/AI 暂缓设计与执行。
- 仅主键索引默认创建；其他索引由业务评估后选择创建（建议随变更记录登记）。

## 8. 变更记录（全局）
- v1：新增 user_profile 表的设计与 DDL；建立数据库设计规范文档（当前文件）。
- v2：将表名调整为 sys_user（原 user_profile），并更新示例索引命名与 DDL。
- v3：为 sys_user 增加 password 字段，并更新 DDL 与字段说明。
- v4：按标准化结构重构文档；新增表设计与 DDL 草案：sys_role、sys_permission、sys_menu、sys_user_role、sys_role_permission、sys_menu_role；sys_log、ai_generation_log 标记为暂缓设计，移除当前阶段的 SQL 草案。