package cn.shoanadmin.web.controller;


import cn.shoanadmin.common.annotation.RequireAuth;
import cn.shoanadmin.common.enums.BusinessCodeEnum;
import cn.shoanadmin.common.exception.BusinessException;
import cn.shoanadmin.domain.api.ApiResult;
import cn.shoanadmin.domain.dto.UserContext;
import cn.shoanadmin.domain.entity.WechatUser;
import cn.shoanadmin.domain.request.LoginReq;
import cn.shoanadmin.domain.request.UpdateUserNicknameReq;
import cn.shoanadmin.domain.response.LoginRes;
import cn.shoanadmin.domain.response.UserProfileRes;
import cn.shoanadmin.service.WechatAuthService;
import cn.shoanadmin.service.WechatUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理小程序登录、用户信息获取等认证相关接口
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final WechatAuthService wechatAuthService;
    private final WechatUserService wechatUserService;

    /**
     * 小程序登录接口
     * 通过微信授权码获取用户信息并生成访问令牌
     * 
     * @param request 登录请求参数
     * @return 登录结果，包含访问令牌和用户信息
     */
    @PostMapping("/miniapp/login")
    public ApiResult<LoginRes> miniappLogin(@RequestBody LoginReq request, HttpServletRequest httpRequest) {
        ApiResult<LoginRes> loginRes = wechatAuthService.miniAppLogin(request, httpRequest);
        return loginRes;
    }

    /**
     * 获取当前用户信息
     * 需要在请求头中携带有效的访问令牌
     * 
     * @return 用户信息
     */
    @GetMapping("/user/info")
    public ApiResult<UserProfileRes> getUserInfo() {
        try {
            // 从上下文获取当前用户ID
            String userId = UserContext.getCurrentUserId();
            if (!StringUtils.hasText(userId)) {
                log.warn("获取用户信息失败：用户未登录");
                throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
            }

            // 查询用户信息
            WechatUser user = wechatUserService.findById(userId);
            if (user == null) {
                log.warn("获取用户信息失败：用户不存在，userId={}", userId);
                throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
            }

            log.info("获取用户信息成功：userId={}", userId);
            UserProfileRes profileRes = UserProfileRes.builder().nickname(user.getNickname()).avatarUrl(user.getAvatarUrl()).build();
            return ApiResult.success(profileRes);

        } catch (BusinessException e) {
            log.error("获取用户信息失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
        }
    }

//    /**
//     * 刷新访问令牌
//     * 使用当前有效的令牌生成新的令牌
//     *
//     * @return 新的访问令牌
//     */
//    @PostMapping("/token/refresh")
//    public ApiResult<Map<String, Object>> refreshToken() {
//        try {
//            // 从上下文获取当前用户ID
//            String userId = UserContext.getCurrentUserId();
//
//            if (!StringUtils.hasText(userId)) {
//                log.warn("刷新令牌失败：用户未登录");
//                throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
//            }
//
//            // 验证用户是否存在
//            WechatUser user = wechatUserService.findById(userId);
//            if (user == null) {
//                log.warn("刷新令牌失败：用户不存在，userId={}", userId);
//                throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
//            }
//
//            // 生成新的访问令牌
//            TokenUtil tokenUtil = new TokenUtil();
//            String newAccessToken = tokenUtil.generateToken(userId, "miniapp");
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("accessToken", newAccessToken);
//
//            log.info("刷新令牌成功：userId={}", userId);
//            return ApiResult.success(result);
//
//        } catch (BusinessException e) {
//            log.error("刷新令牌失败：{}", e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            log.error("刷新令牌异常", e);
//            throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
//        }
//    }

    /**
     * 退出登录
     * 清除用户上下文信息
     * 
     * @return 退出结果
     */
    @PostMapping("/logout")
    public ApiResult<String> logout() {
        try {
            // 从上下文获取当前用户ID
            String userId = UserContext.getCurrentUserId();
            if (StringUtils.hasText(userId)) {
                log.info("用户退出登录：userId={}", userId);
            }
            
            // 清除用户上下文
            UserContext.clear();
            
            return ApiResult.success("退出登录成功");
            
        } catch (Exception e) {
            log.error("退出登录异常", e);
            return ApiResult.success("退出登录成功"); // 即使异常也返回成功，避免客户端错误
        }
    }

    /**
     * 更新用户昵称
     * 需要在请求头中携带有效的访问令牌
     * 
     * @param request 更新昵称请求
     * @return 更新后的用户信息
     */
    @PutMapping("/user/nickname")
    @RequireAuth
    public ApiResult<Boolean> updateUserNickname(@Valid @RequestBody UpdateUserNicknameReq request) {
        return wechatUserService.updateUserNickname(request);
    }
}