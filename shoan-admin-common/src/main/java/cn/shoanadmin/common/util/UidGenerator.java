package cn.shoanadmin.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.function.Predicate;

/**
 * 用户ID生成器
 * 生成规则：FP{当前时间戳前4位}{随机数4位}
 * 示例：FP17351234 (时间戳前4位：1735，随机数：1234)
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Slf4j
@UtilityClass
public class UidGenerator {

    private static final String PREFIX = "FP";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成唯一用户ID
     * 
     * @return 用户ID，格式：FP{时间戳前4位}{随机数4位}
     */
    public String generateUserId() {
        // 获取当前时间戳的前4位
        long timestamp = Instant.now().getEpochSecond();
        String timestampStr = String.valueOf(timestamp);
        String timestampPart = timestampStr.substring(0, Math.min(4, timestampStr.length()));
        
        // 如果时间戳不足4位，用0补齐
        while (timestampPart.length() < 4) {
            timestampPart = "0" + timestampPart;
        }
        
        // 生成4位随机数
        int randomNum = RANDOM.nextInt(10000); // 0-9999
        String randomPart = String.format("%04d", randomNum);
        
        return PREFIX + timestampPart + randomPart;
    }

    /**
     * 生成唯一用户ID（带重试机制）
     * 如果生成的ID已存在，会重新生成
     * 
     * @param existsChecker ID存在性检查器
     * @param maxRetries 最大重试次数
     * @return 唯一的用户ID
     */
    public String generateUniqueUserId(Predicate<String> existsChecker, int maxRetries) {
        String userId;
        int retries = 0;
        
        do {
            userId = generateUserId();
            retries++;
            
            if (retries > maxRetries) {
                log.warn("generateUniqueUser error, maxRetries:{}", maxRetries);
            }
        } while (existsChecker.test(userId));
        
        return userId;
    }

    /**
     * 生成唯一用户ID（默认重试3次）
     * 
     * @param existsChecker ID存在性检查器
     * @return 唯一的用户ID
     */
    public String generateUniqueUserId(Predicate<String> existsChecker) {
        return generateUniqueUserId(existsChecker, 3);
    }

    /**
     * 验证用户ID格式是否正确
     * 
     * @param userId 用户ID
     * @return true-格式正确，false-格式错误
     */
    public boolean isValidUserId(String userId) {
        if (userId == null || userId.length() != 10) {
            return false;
        }
        
        if (!userId.startsWith(PREFIX)) {
            return false;
        }
        
        String numberPart = userId.substring(2);
        try {
            Integer.parseInt(numberPart);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 从用户ID中提取时间戳部分
     * 
     * @param userId 用户ID
     * @return 时间戳部分（4位数字）
     */
    public String extractTimestampPart(String userId) {
        if (!isValidUserId(userId)) {
            throw new IllegalArgumentException("无效的用户ID格式: " + userId);
        }
        return userId.substring(2, 6);
    }

    /**
     * 从用户ID中提取随机数部分
     * 
     * @param userId 用户ID
     * @return 随机数部分（4位数字）
     */
    public String extractRandomPart(String userId) {
        if (!isValidUserId(userId)) {
            throw new IllegalArgumentException("无效的用户ID格式: " + userId);
        }
        return userId.substring(6, 10);
    }
}