package cn.shoanadmin.web.interceptor;

import cn.shoanadmin.common.annotation.RequireAuth;
import cn.shoanadmin.common.enums.BusinessCodeEnum;
import cn.shoanadmin.common.exception.BusinessException;
import cn.shoanadmin.common.util.TokenUtil;
import cn.shoanadmin.domain.dto.UserContext;
import cn.shoanadmin.domain.entity.WechatUser;
import cn.shoanadmin.service.WechatUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 认证拦截器
 * 用于拦截需要认证的请求，验证用户身份
 *
 * @author FruitPieces Team
 * @version 1.0.0
 */
@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private WechatUserService wechatUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是HandlerMethod，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查方法是否需要认证
        RequireAuth requireAuth = method.getAnnotation(RequireAuth.class);
        if (requireAuth == null) {
            // 检查类级别的注解
            requireAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
        }

        // 如果不需要认证，直接放行
        if (requireAuth == null) {
            return true;
        }

        // 提取访问令牌
        String token = request.getHeader("en-bit-token");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_MISSING);
        }

        try {
            // 验证令牌并获取用户ID
            String userId = TokenUtil.getUserIdFromToken(token);
            if (!StringUtils.hasText(userId)) {
                throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
            }

            // 查询用户信息
            WechatUser user = wechatUserService.findById(userId);
            if (user == null) {
                throw new BusinessException(BusinessCodeEnum.AUTH_USER_NOT_FOUND);
            }

            // 设置用户上下文
            UserContext.setCurrentUser(user);
            return true;

        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户上下文
        UserContext.clear();
    }
}