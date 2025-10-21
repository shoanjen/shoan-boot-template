package cn.shoanadmin.service.impl;

import cn.shoanadmin.common.enums.BusinessCodeEnum;
import cn.shoanadmin.common.exception.BusinessException;
import cn.shoanadmin.common.util.UidGenerator;
import cn.shoanadmin.domain.api.ApiResult;
import cn.shoanadmin.domain.dto.UserContext;
import cn.shoanadmin.domain.entity.WechatUser;
import cn.shoanadmin.domain.request.UpdateUserNicknameReq;
import cn.shoanadmin.infrastructure.manager.WechatUserManager;
import cn.shoanadmin.infrastructure.mapper.WechatUserMapper;
import cn.shoanadmin.service.WechatUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 微信用户服务
 * 负责微信用户的增删改查操作
 *
 * @author FruitPieces
 * @since 2024-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatUserServiceImpl extends ServiceImpl<WechatUserMapper, WechatUser> implements WechatUserService {

    private final WechatUserManager wechatUserManager;

    /**
     * 根据openid查询用户
     *
     * @param openid 微信openid
     * @return 用户信息，不存在返回null
     */
    public WechatUser findByOpenid(String openid) {
        if (!StringUtils.hasText(openid)) {
            log.warn("查询用户失败：openid为空");
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }

        log.info("根据openid查询用户：{}", openid);
        WechatUser wechatUser = wechatUserManager.findByOpenid(openid);
        return wechatUser;
    }

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息，不存在返回null
     */
    public WechatUser findById(String userId) {
        if (!StringUtils.hasText(userId)) {
            log.warn("查询用户失败：用户ID为空");
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }
        WechatUser wechatUser = wechatUserManager.findByUserId(userId);
        return wechatUser;
    }

    /**
     * 创建新用户
     *
     * @param openid     微信openid
     * @param sessionKey 会话密钥
     * @param nickname   用户昵称（可选）
     * @param avatarUrl  头像URL（可选）
     * @return 创建的用户信息
     */
    public WechatUser createUser(String openid, String sessionKey, String nickname, String avatarUrl) {
        if (!StringUtils.hasText(openid)) {
            log.error("创建用户失败：openid不能为空");
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }

        if (!StringUtils.hasText(sessionKey)) {
            log.error("创建用户失败：sessionKey不能为空");
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }

        try {
            // 生成唯一用户ID
            String userId = UidGenerator.generateUserId();

            // 创建用户对象
            WechatUser wechatUser = WechatUser.builder()
                    .id(userId)
                    .openid(openid)
                    .sessionKey(sessionKey)
                    .nickname(StringUtils.hasText(nickname) ? nickname : "微信用户")
                    .avatarUrl(StringUtils.hasText(avatarUrl) ? avatarUrl : "")
                    .status(1)
                    .createdTime(System.currentTimeMillis())
                    .updatedTime(System.currentTimeMillis()).build();
            wechatUserManager.createUser(wechatUser);
            return wechatUser;
        } catch (Exception e) {
            log.error("创建用户失败：openid={}", openid, e);
            throw new BusinessException(BusinessCodeEnum.WECHAT_LOGIN_FAILED);
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId     用户ID
     * @param nickname   昵称
     * @param avatarUrl  头像URL
     * @param sessionKey 会话密钥
     * @return 更新后的用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public WechatUser updateUser(String userId, String nickname, String avatarUrl, String sessionKey) {
        if (!StringUtils.hasText(userId)) {
            log.error("更新用户失败：用户ID不能为空");
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }

        // 查询现有用户
        WechatUser existingUser = findById(userId);
        if (existingUser == null) {
            log.error("更新用户失败：用户不存在，userId={}", userId);
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }

        try {
            // 更新用户信息
            if (StringUtils.hasText(nickname)) {
                existingUser.setNickname(nickname);
            }
            if (StringUtils.hasText(avatarUrl)) {
                existingUser.setAvatarUrl(avatarUrl);
            }
            if (StringUtils.hasText(sessionKey)) {
                existingUser.setSessionKey(sessionKey);
            }

            // 更新最后登录时间
            existingUser.setLastLoginTime(System.currentTimeMillis());
            existingUser.setUpdatedTime(System.currentTimeMillis());
            wechatUserManager.updateUser(existingUser);
            log.info("更新用户成功：userId={}", userId);
            return existingUser;

        } catch (Exception e) {
            log.error("更新用户失败：userId={}", userId, e);
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }
    }

    /**
     * 更新用户登录信息
     *
     * @param userId    用户ID
     * @param loginIp   登录IP
     * @param userAgent 用户代理
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginInfo(String userId, String loginIp, String userAgent) {
        if (!StringUtils.hasText(userId)) {
            log.error("更新登录信息失败：用户ID不能为空");
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }

        WechatUser user = findById(userId);
        if (user == null) {
            log.error("更新登录信息失败：用户不存在，userId={}", userId);
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }

        try {
            user.setLastLoginTime(System.currentTimeMillis());
            user.setLastLoginIp(loginIp);
            user.setUserAgent(userAgent);
            user.setUpdatedTime(System.currentTimeMillis());
            wechatUserManager.updateUser(user);
            log.info("更新用户登录信息成功：userId={}", userId);

        } catch (Exception e) {
            log.error("更新用户登录信息失败：userId={}", userId, e);
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }
    }

    /**
     * 更新用户昵称
     * @param request 新昵称
     * @return 更新后的用户信息
     */
    public ApiResult<Boolean> updateUserNickname(UpdateUserNicknameReq request) {
        try {
            // 从上下文获取当前用户ID
            String userId = UserContext.getCurrentUserId();
            if (!StringUtils.hasText(userId)) {
                log.warn("更新用户昵称失败：用户未登录");
                throw new BusinessException(BusinessCodeEnum.AUTH_TOKEN_INVALID);
            }

            // 更新用户昵称
            if (!StringUtils.hasText(request.getNickname())) {
                log.error("更新用户昵称失败：昵称不能为空");
                throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
            }

            // 查询现有用户
            WechatUser existingUser = findById(userId);
            if (existingUser == null) {
                log.error("更新用户昵称失败：用户不存在，userId={}", userId);
                throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
            }

            // 更新昵称
            existingUser.setNickname(request.getNickname());
            existingUser.setId(userId);
            existingUser.setUpdatedTime(System.currentTimeMillis());
            int rows = wechatUserManager.updateUser(existingUser);
            log.info("更新用户昵称成功：userId={}, nickname={}", userId, request.getNickname());
            return ApiResult.success(rows > 0);
        } catch (BusinessException e) {
            log.error("更新用户昵称失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新用户昵称异常", e);
            throw new BusinessException(BusinessCodeEnum.PARAM_ERROR);
        }
    }
}