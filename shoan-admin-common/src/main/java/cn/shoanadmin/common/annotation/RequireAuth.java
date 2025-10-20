package cn.shoanadmin.common.annotation;

import java.lang.annotation.*;

/**
 * 需要认证注解
 * 用于标记需要用户登录认证的接口或方法
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAuth {

    /**
     * 是否必须认证
     * 默认为true，表示必须认证
     * 
     * @return true-必须认证，false-可选认证
     */
    boolean required() default true;

    /**
     * 认证失败时的错误信息
     * 
     * @return 错误信息
     */
    String message() default "请先登录";

    /**
     * 是否允许匿名访问
     * 当设置为true时，即使没有认证也允许访问，但会尝试解析用户信息
     * 
     * @return true-允许匿名访问，false-不允许匿名访问
     */
    boolean allowAnonymous() default false;
}