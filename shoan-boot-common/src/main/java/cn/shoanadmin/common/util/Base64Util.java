package cn.shoanadmin.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64工具类
 * 提供Base64编码和解码功能
 *
 * @author fruitpieces
 * @since 2024-01-01
 */
@UtilityClass
@Slf4j
public class Base64Util {

    /**
     * 对字符串进行Base64编码
     *
     * @param source 原始字符串
     * @return Base64编码后的字符串
     */
    public static String encode(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }
        try {
            return Base64.getEncoder().encodeToString(source.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("base64 encode error", e);
            return null;
        }
    }

    /**
     * 对字节数组进行Base64编码
     *
     * @param bytes 原始字节数组
     * @return Base64编码后的字符串
     */
    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对Base64编码的字符串进行解码
     *
     * @param encoded Base64编码的字符串
     * @return 解码后的原始字符串
     */
    public static String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return encoded;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encoded);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对Base64编码的字符串进行解码，返回字节数组
     *
     * @param encoded Base64编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decodeToBytes(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return new byte[0];
        }
        try {
            return Base64.getDecoder().decode(encoded);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * URL安全的Base64编码
     *
     * @param source 原始字符串
     * @return URL安全的Base64编码字符串
     */
    public static String encodeUrlSafe(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(source.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * URL安全的Base64编码
     *
     * @param bytes 原始字节数组
     * @return URL安全的Base64编码字符串
     */
    public static String encodeUrlSafe(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * URL安全的Base64解码
     *
     * @param encoded URL安全的Base64编码字符串
     * @return 解码后的原始字符串
     */
    public static String decodeUrlSafe(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return encoded;
        }
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encoded);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * URL安全的Base64解码，返回字节数组
     *
     * @param encoded URL安全的Base64编码字符串
     * @return 解码后的字节数组
     */
    public static byte[] decodeUrlSafeToBytes(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return new byte[0];
        }
        try {
            return Base64.getUrlDecoder().decode(encoded);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证字符串是否为有效的Base64编码
     *
     * @param encoded 待验证的字符串
     * @return true表示有效，false表示无效
     */
    public static boolean isValidBase64(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return false;
        }
        try {
            Base64.getDecoder().decode(encoded);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证字符串是否为有效的URL安全Base64编码
     *
     * @param encoded 待验证的字符串
     * @return true表示有效，false表示无效
     */
    public static boolean isValidUrlSafeBase64(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return false;
        }
        try {
            Base64.getUrlDecoder().decode(encoded);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}