package cn.shoanadmin.domain.dto;


import cn.shoanadmin.domain.entity.WechatUser;

/**
 * 用户上下文
 * 管理当前登录用户信息，使用ThreadLocal确保线程安全
 */
public class UserContext {

    private static final ThreadLocal<WechatUser> USER_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户
     *
     * @param user 用户信息
     */
    public static void setCurrentUser(WechatUser user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户信息，如果未登录则返回null
     */
    public static WechatUser getCurrentUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID，如果未登录则返回null
     */
    public static String getCurrentUserId() {
        WechatUser user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 设置当前访问令牌
     *
     * @param token 访问令牌
     */
    public static void setCurrentToken(String token) {
        TOKEN_HOLDER.set(token);
    }

    /**
     * 获取当前访问令牌
     *
     * @return 当前访问令牌，如果未设置则返回null
     */
    public static String getCurrentToken() {
        return TOKEN_HOLDER.get();
    }

    /**
     * 检查当前是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * 清除当前用户上下文
     * 通常在请求结束时调用
     */
    public static void clear() {
        USER_HOLDER.remove();
        TOKEN_HOLDER.remove();
    }

    /**
     * 获取当前用户的昵称
     *
     * @return 用户昵称，如果未登录或昵称为空则返回"匿名用户"
     */
    public static String getCurrentUserNickname() {
        WechatUser user = getCurrentUser();
        if (user != null && user.getNickname() != null && !user.getNickname().trim().isEmpty()) {
            return user.getNickname();
        }
        return "匿名用户";
    }

    /**
     * 获取当前用户的OpenID
     *
     * @return 用户OpenID，如果未登录则返回null
     */
    public static String getCurrentUserOpenId() {
        WechatUser user = getCurrentUser();
        return user != null ? user.getOpenid() : null;
    }
}