# 数据库设计规范
1. 建表时需要包含四要素：create_time、update_time、create_by、update_by
2. 与时间有关的字段对应java中的long类型
3. 创建索引时，只需创建id主键唯一索引，其他字段索引由业务方自行选择创建
4. 需要将设计规范保存到当前项目的根目录中。
# 代码编写规范
开发前请先阅读当前项目README.md
1. 接口路径规范
   - 接口路径：前缀/api/v1/模块名/
   - 示例用户信息编辑功能：/api/v1/user/updateUserInfo
2. controller规范：
   - controller中的方法，不要出现业务逻辑处理相关的代码，统一放到serviceImpl对应的实现类中的方法处理
   - controller中的接口请求体采用xxxReq同时需要继承BaseReq；
   - controller中的接口内容采用xxxRes。
3. 工具类规范：
   - 在工具类上标识lombok的注解@UtilityClass,具体的方法上就无需static关键字标识
4. 注释规范：
   - 关键功能代码要有具体的注释
   - 类注释模板：
   ```java
   /**
   *@author enbit-zse
   *@description 类的具体作用描述
   *@since 2025-xx-xx 当前年月日
   */
    ```
   - 方法注释模板

   ```java
    /**
    *@param 方法参数
    *@description 方法的具体作用描述
    *@return 方法返回值类型
    */  
   ```
   - 实体类相关上的字段要有具体的注释

   ```java
    /**
    */
   ```
6. 依赖注入规范：
- 在使用其他Bean时采用的是lombok注解中的@RequiredArgsConstructor，因此无需用@Autowired注解标识,下方代码块为使用案例
```java
@RestController
@RequestMapping("/api/v1/user")
public class SysUserController {
    private final SysUserService sysUserService;
}
```

7. 实体类规范
  - 实体类上标识lombok的注解@Data,具体的字段上就无需再标识getter/setter方法了
  - 实体类上标识lombok的注解@Builder,用于构建对象
  - 实体类上标识lombok的注解@AllArgsConstructor,用于全参构造
  - 实体类上标识lombok的注解@NoArgsConstructor,用于无参构造


