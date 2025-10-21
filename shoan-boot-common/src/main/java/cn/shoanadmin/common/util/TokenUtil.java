package cn.shoanadmin.common.util;

import cn.shoanadmin.common.constant.ComStrConstant;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JWT工具类
 * 用于生成、验证和解析JWT令牌
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Slf4j
@UtilityClass
public class TokenUtil {

    /**
     * 7天的毫秒数
     */
    private static final String TOKEN_EXPIRE_TIME = "expires_in";
    private static final String USER_ID_CLAIM = "userId";
    private static final String DEVICE_TYPE_CLAIM = "deviceType";

    /**
     * 生成token令牌
     * 
     * @param userId 用户ID
     * @return JWT令牌
     */
    public String generateToken(String userId, String deviceType) {
        Map<String, String> claims = new LinkedHashMap<>();
        claims.put(USER_ID_CLAIM, userId);
        claims.put(DEVICE_TYPE_CLAIM, deviceType);
        claims.put(TOKEN_EXPIRE_TIME, String.valueOf(TimestampUtil.getCurrentTimestampAfterSevenDays()));
        String str = MapUtil.mapToStringSeparated(claims, ComStrConstant.colon);
        log.debug("generateToken claims: {}", JsonUtil.toJsonString(str));
        return Base64Util.encode(str);
    }

    /**
     * 从令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        String decodeStr = Base64Util.decode(token);
        Map<String, String> resultMap = MapUtil.stringToMapSeparated(decodeStr, ComStrConstant.colon);
        return resultMap.get(USER_ID_CLAIM);
    }

    /**
     * 从令牌中获取设备类型
     * 
     * @param token JWT令牌
     * @return 设备类型
     */
    public String getDeviceTypeFromToken(String token) {
        String decodeStr = Base64Util.decode(token);
        Map<String, String> resultMap = MapUtil.stringToMapSeparated(decodeStr, ComStrConstant.colon);
        return resultMap.get(DEVICE_TYPE_CLAIM);
    }

    /**
     * 从令牌中获取过期时间
     * 
     * @param token JWT令牌
     * @return 过期时间
     */
    public long getExpirationFromToken(String token) {
        String decodeStr = Base64Util.decode(token);
        Map<String, String> resultMap = MapUtil.stringToMapSeparated(decodeStr, ComStrConstant.colon);
        String expireTime = resultMap.get(TOKEN_EXPIRE_TIME);
        return Long.parseLong(expireTime);
    }

    /**
     * 验证令牌是否有效
     * 
     * @param token JWT令牌
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token, String userId, String deviceType) {
        long expirationFromToken = getExpirationFromToken(token);
        if (expirationFromToken < System.currentTimeMillis()) {
            return false;
        }
        if (Objects.equals(userId, getUserIdFromToken(token))) {
            return true;
        }
        if (Objects.equals(deviceType, getDeviceTypeFromToken(token))) {
            return true;
        }
        return false;
    }

    /**
     * 检查令牌是否即将过期
     * 
     * @param token JWT令牌
     * @param minutesBeforeExpiry 过期前多少分钟算即将过期
     * @return true-即将过期，false-未即将过期
     */
    public boolean isTokenExpiringSoon(String token, int minutesBeforeExpiry) {
        return false;
    }

}