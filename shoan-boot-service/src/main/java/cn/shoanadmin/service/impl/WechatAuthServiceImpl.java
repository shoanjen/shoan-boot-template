package cn.shoanadmin.service.impl;


import cn.shoanadmin.common.config.WechatMiniappConfig;
import cn.shoanadmin.common.enums.BusinessCodeEnum;
import cn.shoanadmin.common.exception.BusinessException;
import cn.shoanadmin.common.util.IpUtil;
import cn.shoanadmin.common.util.TokenUtil;
import cn.shoanadmin.domain.api.ApiResult;
import cn.shoanadmin.domain.entity.WechatUser;
import cn.shoanadmin.domain.request.LoginReq;
import cn.shoanadmin.domain.response.LoginRes;
import cn.shoanadmin.service.WechatAuthService;
import cn.shoanadmin.service.WechatUserService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * 微信认证服务
 * 负责与微信服务器交互，获取用户openid和session_key
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatAuthServiceImpl implements WechatAuthService {

    private final WechatMiniappConfig wechatConfig;
    private final RestTemplate restTemplate;
    private final WechatUserService wechatUserService;

    /**
     * 微信登录响应结果
     */
    @Data
    public static class WechatLoginResult {
        private String openid;
        private String sessionKey;
        private String unionid;
        private Integer errcode;
        private String errmsg;
        /**
         * 检查是否成功
         * @return true-成功，false-失败
         */
        public boolean isSuccess() {
            return errcode == null || errcode == 0;
        }
    }

    @Override
    public ApiResult<LoginRes> miniAppLogin(LoginReq request, HttpServletRequest httpRequest) {
        try {
            // 参数验证
            if (!StringUtils.hasText(request.getCode())) {
                log.warn("小程序登录失败：授权码为空");
                throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
            }

            log.info("开始小程序登录：code={}", request.getCode());

            // 调用微信接口获取用户信息
            WechatLoginResult authResult = getWechatUserInfo(request.getCode());

            // 查询用户是否已存在
            WechatUser existingUser = wechatUserService.findByOpenid(authResult.getOpenid());

            WechatUser user = null;
            if (existingUser == null) {
                // 用户不存在，创建新用户
                log.info("用户不存在，创建新用户：openid={}", authResult.getOpenid());
                user = wechatUserService.createUser(
                        authResult.getOpenid(),
                        authResult.getSessionKey(),
                        request.getNickname(),
                        request.getAvatarUrl()
                );
            } else {
                user = existingUser;
            }

            // 更新登录信息
            String clientIp = IpUtil.getClientIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            if (user != null) {
                wechatUserService.updateLoginInfo(user.getId(), clientIp, userAgent);
                // 生成访问令牌
                String accessToken = TokenUtil.generateToken(user.getId(), "miniapp");

                LoginRes loginRes = buildUserInfo(user, accessToken);

                log.info("小程序登录成功：userId={}, openid={}", user.getId(), user.getOpenid());
                return ApiResult.success(loginRes);
            }
        } catch (Exception e) {
            log.error("小程序登录异常", e);
            return ApiResult.error(BusinessCodeEnum.WECHAT_LOGIN_FAILED);
        }
        return ApiResult.error(BusinessCodeEnum.WECHAT_LOGIN_FAILED);
    }

    /**
     * 构建用户信息返回对象
     *
     * @param user 用户实体
     * @return 用户信息Map
     */
    private LoginRes buildUserInfo(WechatUser user, String accessToken) {
        LoginRes build = LoginRes.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .lastLoginTime(user.getLastLoginTime())
                .accessToken(accessToken)
                .build();
        return build;
    }

    /**
     * 通过微信登录凭证code获取用户openid和session_key
     * 
     * @param code 微信登录凭证
     * @return 微信登录结果
     * @throws BusinessException 当微信API调用失败时抛出
     */
    private WechatLoginResult getWechatUserInfo(String code) {
        // 参数验证
        if (!StringUtils.hasText(code)) {
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR, "微信登录凭证不能为空");
        }

        // 构建请求URL
        String url = buildJscode2sessionUrl(code);
        
        try {
            log.info("调用微信jscode2session接口，code: {}", code);
            
            // 调用微信API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();
            
            log.info("微信jscode2session接口响应: {}", responseBody);
            
            // 解析响应结果
            WechatLoginResult result = parseWechatResponse(responseBody);
            
            // 检查响应结果
            if (!result.isSuccess()) {
                log.error("微信登录失败，errcode: {}, errmsg: {}", result.getErrcode(), result.getErrmsg());
                throw new BusinessException(BusinessCodeEnum.WECHAT_LOGIN_FAILED, 
                    "微信登录失败: " + result.getErrmsg());
            }
            
            // 验证必要字段
            if (!StringUtils.hasText(result.getOpenid())) {
                log.error("微信返回的openid为空");
                throw new BusinessException(BusinessCodeEnum.WECHAT_LOGIN_FAILED, "微信返回的用户标识为空");
            }
            
            log.info("微信登录成功，openid: {}", result.getOpenid());
            return result;
            
        } catch (Exception e) {
            log.error("调用微信jscode2session接口异常", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(BusinessCodeEnum.WECHAT_API_ERROR, "微信服务异常: " + e.getMessage());
        }
    }

    /**
     * 构建jscode2session接口URL
     * 
     * @param code 微信登录凭证
     * @return 完整的接口URL
     */
    private String buildJscode2sessionUrl(String code) {
        return String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatConfig.getJscode2sessionUrl(),
                wechatConfig.getAppId(),
                wechatConfig.getAppSecret(),
                code);
    }

    /**
     * 解析微信API响应结果
     * 
     * @param responseBody 响应体
     * @return 解析后的结果对象
     */
    private WechatLoginResult parseWechatResponse(String responseBody) {
        try {
            JSONObject jsonObject = JSON.parseObject(responseBody);
            
            WechatLoginResult result = new WechatLoginResult();
            result.setOpenid(jsonObject.getString("openid"));
            result.setSessionKey(jsonObject.getString("session_key"));
            result.setUnionid(jsonObject.getString("unionid"));
            result.setErrcode(jsonObject.getInteger("errcode"));
            result.setErrmsg(jsonObject.getString("errmsg"));
            
            return result;
            
        } catch (Exception e) {
            log.error("解析微信API响应失败: {}", responseBody, e);
            throw new BusinessException(BusinessCodeEnum.WECHAT_API_ERROR, "解析微信响应失败");
        }
    }

    /**
     * 验证微信用户数据的完整性
     * 
     * @param sessionKey 会话密钥
     * @param encryptedData 加密数据
     * @param iv 初始向量
     * @return 解密后的用户信息
     */
    public JSONObject decryptUserInfo(String sessionKey, String encryptedData, String iv) {
        // TODO: 实现微信用户数据解密逻辑
        // 这里需要使用AES解密算法解密encryptedData
        // 暂时返回空对象，后续可以根据需要实现
        log.warn("微信用户数据解密功能暂未实现");
        return new JSONObject();
    }
}