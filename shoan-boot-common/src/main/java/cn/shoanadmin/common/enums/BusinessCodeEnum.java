package cn.shoanadmin.common.enums;

import lombok.Getter;

/**
 * 业务错误码枚举
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Getter
public enum BusinessCodeEnum {

    // 认证相关错误
    AUTH_TOKEN_NOT_EXIST("AU001", "访问令牌不存在"),
    AUTH_TOKEN_MISSING("AU002", "访问令牌缺失"),
    AUTH_TOKEN_INVALID("AU003", "访问令牌无效"),
    AUTH_TOKEN_EXPIRED("AU004", "访问令牌已过期"),
    AUTH_USER_NOT_FOUND("AU005", "用户不存在"),
    AUTH_USER_DISABLED("AU006", "用户已被禁用"),

    // 参数相关错误
    PARAM_ERROR("PA001", "参数错误"),

    // 微信相关错误
    WECHAT_LOGIN_FAILED("WE001", "微信登录失败"),
    WECHAT_API_ERROR("WE002", "微信接口调用失败"),

    // 收藏分类相关错误
    CATEGORY_NAME_EXISTS("CA001", "分类名称已存在"),
    CATEGORY_NOT_EXISTS("CA002", "分类不存在"),
    SYSTEM_CATEGORY_NOT_EDITABLE("CA003", "系统分类不允许修改"),
    SYSTEM_CATEGORY_NOT_DELETABLE("CA004", "系统分类不允许删除"),

    // 系统相关错误
    SYSTEM_ERROR("SY001", "系统错误");

    private final String code;
    private final String message;

    BusinessCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
