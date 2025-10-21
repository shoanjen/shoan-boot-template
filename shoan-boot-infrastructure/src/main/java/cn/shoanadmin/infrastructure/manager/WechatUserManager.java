package cn.shoanadmin.infrastructure.manager;

import cn.shoanadmin.common.enums.BusinessCodeEnum;
import cn.shoanadmin.common.exception.BusinessException;
import cn.shoanadmin.domain.entity.WechatUser;
import cn.shoanadmin.infrastructure.mapper.WechatUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class WechatUserManager {
    private final WechatUserMapper wechatUserMapper;

    public WechatUser findByOpenid(String openid) {
        log.info("根据openid查询用户：{}", openid);
        LambdaQueryWrapper<WechatUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WechatUser::getOpenid, openid);
        WechatUser wechatUser = wechatUserMapper.selectOne(queryWrapper);
        return wechatUser;
    }

    public WechatUser findByUserId(String userId) {
        log.info("根据用户ID查询用户：{}", userId);
        LambdaQueryWrapper<WechatUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WechatUser::getId, userId);
        WechatUser wechatUser = wechatUserMapper.selectOne(queryWrapper);
        return wechatUser;
    }

    public void createUser(WechatUser wechatUser) {
        log.info("创建用户：{}", wechatUser);
        wechatUserMapper.insert(wechatUser);
    }

    public int updateUser(WechatUser wechatUser) {
        log.info("更新用户：{}", wechatUser);
        return wechatUserMapper.updateById(wechatUser);
    }

}
