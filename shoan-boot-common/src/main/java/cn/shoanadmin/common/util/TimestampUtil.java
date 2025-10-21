package cn.shoanadmin.common.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳工具类
 * 提供时间戳相关的常用操作方法
 *
 * @author fruitpieces
 * @since 2024-01-01
 */
@UtilityClass
public class TimestampUtil {

    /**
     * 默认时区 - 东八区
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 默认日期时间格式
     */
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 7天的毫秒数
     */
    private static final long SEVEN_DAYS_MILLIS = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 获取当前时间戳（毫秒级别）
     *
     * @return 当前时间戳毫秒值
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取以当前时间戳为基础7天之后的时间戳（毫秒级别）
     *
     * @return 7天后的时间戳毫秒值
     */
    public static long getCurrentTimestampAfterSevenDays() {
        return getCurrentTimestamp() + SEVEN_DAYS_MILLIS;
    }

    /**
     * 获取以指定时间戳为基础7天之后的时间戳（毫秒级别）
     *
     * @param inputTimestamp 基础时间戳
     * @return 7天后的时间戳毫秒值
     */
    public static long getInputTimestampAfterSevenDays(long inputTimestamp) {
        return inputTimestamp + SEVEN_DAYS_MILLIS;
    }

    /**
     * 将当前时间戳转为年月日时分秒（默认格式：yyyy-MM-dd HH:mm:ss）
     *
     * @return 格式化后的日期时间字符串
     */
    public static String formatCurrentTimestamp() {
        return formatInputTimestamp(getCurrentTimestamp(), DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 将当前时间戳转为年月日时分秒（自定义格式）
     *
     * @param pattern 日期时间格式模式
     * @return 格式化后的日期时间字符串
     */
    public static String formatCurrentTimestamp(String pattern) {
        return formatInputTimestamp(getCurrentTimestamp(), pattern);
    }

    /**
     * 将传入时间戳转为年月日时分秒（默认格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return 格式化后的日期时间字符串
     */
    public static String formatInputTimestamp(long inputTimestamp) {
        return formatInputTimestamp(inputTimestamp, DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 将传入时间戳转为年月日时分秒（自定义格式）
     *
     * @param inputTimestamp 时间戳毫秒值
     * @param pattern   日期时间格式模式
     * @return 格式化后的日期时间字符串
     */
    public static String formatInputTimestamp(long inputTimestamp, String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            pattern = DEFAULT_DATETIME_PATTERN;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(inputTimestamp), 
                DEFAULT_ZONE_ID
            );
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前时间戳的年份
     *
     * @return 年份
     */
    public static int getCurrentYear() {
        return getInputYear(getCurrentTimestamp());
    }

    /**
     * 获取传入时间戳的年份
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return 年份
     */
    public static int getInputYear(long inputTimestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(inputTimestamp), 
                DEFAULT_ZONE_ID
            );
            return dateTime.getYear();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取当前时间戳的月份
     *
     * @return 月份（1-12）
     */
    public static int getCurrentMonth() {
        return getInputMonth(getCurrentTimestamp());
    }

    /**
     * 获取传入时间戳的月份
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return 月份（1-12）
     */
    public static int getInputMonth(long inputTimestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(inputTimestamp), 
                DEFAULT_ZONE_ID
            );
            return dateTime.getMonthValue();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取当前时间戳的日期
     *
     * @return 日期（1-31）
     */
    public static int getCurrentDay() {
        return getInputDay(getCurrentTimestamp());
    }

    /**
     * 获取传入时间戳的日期
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return 日期（1-31）
     */
    public static int getInputDay(long inputTimestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(inputTimestamp), 
                DEFAULT_ZONE_ID
            );
            return dateTime.getDayOfMonth();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取当前时间戳的年月日（默认格式：yyyy-MM-dd）
     *
     * @return 格式化后的年月日字符串
     */
    public static String getCurrentYearMonthDay() {
        return getInputYearMonthDay(getCurrentTimestamp(), DEFAULT_DATE_PATTERN);
    }

    /**
     * 获取当前时间戳的年月日（自定义格式）
     *
     * @param pattern 日期格式模式
     * @return 格式化后的年月日字符串
     */
    public static String getCurrentYearMonthDay(String pattern) {
        return getInputYearMonthDay(getCurrentTimestamp(), pattern);
    }

    /**
     * 获取传入时间戳的年月日（默认格式：yyyy-MM-dd）
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return 格式化后的年月日字符串
     */
    public static String getInputYearMonthDay(long inputTimestamp) {
        return getInputYearMonthDay(inputTimestamp, DEFAULT_DATE_PATTERN);
    }

    /**
     * 获取传入时间戳的年月日（自定义格式）
     *
     * @param inputTimestamp 时间戳毫秒值
     * @param pattern   日期格式模式
     * @return 格式化后的年月日字符串
     */
    public static String getInputYearMonthDay(long inputTimestamp, String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            pattern = DEFAULT_DATE_PATTERN;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(inputTimestamp), 
                DEFAULT_ZONE_ID
            );
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前时间戳的小时
     *
     * @return 小时（0-23）
     */
    public static int getCurrentHour() {
        return getInputHour(getCurrentTimestamp());
    }

    /**
     * 获取传入时间戳的小时
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return 小时（0-23）
     */
    public static int getInputHour(long inputTimestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(inputTimestamp), 
                DEFAULT_ZONE_ID
            );
            return dateTime.getHour();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取当前时间戳的分钟
     *
     * @return 分钟（0-59）
     */
    public static int getCurrentMinute() {
        return getInputMinute(getCurrentTimestamp());
    }

    /**
     * 获取传入时间戳的分钟
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return 分钟（0-59）
     */
    public static int getInputMinute(long inputTimestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(inputTimestamp), 
                DEFAULT_ZONE_ID
            );
            return dateTime.getMinute();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取当前时间戳的秒
     *
     * @return 秒（0-59）
     */
    public static int getCurrentSecond() {
        return getInputSecond(getCurrentTimestamp());
    }

    /**
     * 获取传入时间戳的秒
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return 秒（0-59）
     */
    public static int getInputSecond(long inputTimestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(inputTimestamp), 
                DEFAULT_ZONE_ID
            );
            return dateTime.getSecond();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 判断时间戳是否为今天
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return true表示是今天，false表示不是
     */
    public static boolean isInputTimestampToday(long inputTimestamp) {
        try {
            String today = getCurrentYearMonthDay();
            String targetDay = getInputYearMonthDay(inputTimestamp);
            return today != null && today.equals(targetDay);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断时间戳是否为昨天
     *
     * @param inputTimestamp 时间戳毫秒值
     * @return true表示是昨天，false表示不是
     */
    public static boolean isInputTimestampYesterday(long inputTimestamp) {
        try {
            long yesterdayTimestamp = getCurrentTimestamp() - 24 * 60 * 60 * 1000L;
            String yesterday = getInputYearMonthDay(yesterdayTimestamp);
            String targetDay = getInputYearMonthDay(inputTimestamp);
            return yesterday != null && yesterday.equals(targetDay);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 计算两个时间戳之间的天数差
     *
     * @param inputTimestamp1 时间戳1
     * @param inputTimestamp2 时间戳2
     * @return 天数差（绝对值）
     */
    public static long getDaysBetweenInputTimestamps(long inputTimestamp1, long inputTimestamp2) {
        try {
            long diff = Math.abs(inputTimestamp1 - inputTimestamp2);
            return diff / (24 * 60 * 60 * 1000L);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取当前时间戳指定天数之前的时间戳
     *
     * @param days 天数
     * @return 指定天数之前的时间戳
     */
    public static long getCurrentTimestampDaysAgo(int days) {
        return getCurrentTimestamp() - (days * 24 * 60 * 60 * 1000L);
    }

    /**
     * 获取当前时间戳指定天数之后的时间戳
     *
     * @param days 天数
     * @return 指定天数之后的时间戳
     */
    public static long getCurrentTimestampDaysLater(int days) {
        return getCurrentTimestamp() + (days * 24 * 60 * 60 * 1000L);
    }

    /**
     * 获取指定时间戳指定天数之前的时间戳
     *
     * @param inputTimestamp 基础时间戳
     * @param days 天数
     * @return 指定天数之前的时间戳
     */
    public static long getInputTimestampDaysAgo(long inputTimestamp, int days) {
        return inputTimestamp - (days * 24 * 60 * 60 * 1000L);
    }

    /**
     * 获取指定时间戳指定天数之后的时间戳
     *
     * @param inputTimestamp 基础时间戳
     * @param days 天数
     * @return 指定天数之后的时间戳
     */
    public static long getInputTimestampDaysLater(long inputTimestamp, int days) {
        return inputTimestamp + (days * 24 * 60 * 60 * 1000L);
    }
}