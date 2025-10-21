package cn.shoanadmin.common.util;

import com.alibaba.fastjson2.*;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 * 基于Fastjson2提供JSON序列化和反序列化功能
 *
 * @author fruitpieces
 * @since 2024-01-01
 */
@UtilityClass
public class JsonUtil {

    /**
     * 对象转JSON字符串
     *
     * @param object 待转换的对象
     * @return JSON字符串
     */
    public String toJsonString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象转JSON字符串（格式化输出）
     *
     * @param object 待转换的对象
     * @return 格式化的JSON字符串
     */
    public String toJsonStringPretty(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return JSON.toJSONString(object, JSONWriter.Feature.PrettyFormat);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象转JSON字符串（忽略null值）
     *
     * @param object 待转换的对象
     * @return JSON字符串
     */
    public String toJsonStringIgnoreNull(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return JSON.toJSONString(object, JSONWriter.Feature.NotWriteDefaultValue);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * JSON字符串转对象
     *
     * @param jsonString JSON字符串
     * @param clazz      目标类型
     * @param <T>        泛型类型
     * @return 转换后的对象
     */
    public <T> T parseObject(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.trim().isEmpty() || clazz == null) {
            return null;
        }
        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * JSON字符串转对象（使用TypeReference）
     *
     * @param jsonString    JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型类型
     * @return 转换后的对象
     */
    public <T> T parseObject(String jsonString, TypeReference<T> typeReference) {
        if (jsonString == null || jsonString.trim().isEmpty() || typeReference == null) {
            return null;
        }
        try {
            return JSON.parseObject(jsonString, typeReference);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * JSON字符串转List
     *
     * @param jsonString JSON字符串
     * @param clazz      列表元素类型
     * @param <T>        泛型类型
     * @return 转换后的List
     */
    public <T> List<T> parseArray(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.trim().isEmpty() || clazz == null) {
            return null;
        }
        try {
            return JSON.parseArray(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * JSON字符串转JSONObject
     *
     * @param jsonString JSON字符串
     * @return JSONObject对象
     */
    public JSONObject parseJSONObject(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(jsonString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * JSON字符串转JSONArray
     *
     * @param jsonString JSON字符串
     * @return JSONArray对象
     */
    public JSONArray parseJSONArray(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseArray(jsonString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象转Map
     *
     * @param object 待转换的对象
     * @return Map对象
     */
    public Map<String, Object> objectToMap(Object object) {
        if (object == null) {
            return null;
        }
        try {
            String jsonString = JSON.toJSONString(object);
            return JSON.parseObject(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Map转对象
     *
     * @param map   Map对象
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象
     */
    public <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null || map.isEmpty() || clazz == null) {
            return null;
        }
        try {
            String jsonString = JSON.toJSONString(map);
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证字符串是否为有效的JSON格式
     *
     * @param jsonString 待验证的字符串
     * @return true表示有效，false表示无效
     */
    public boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }
        try {
            JSON.parse(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证字符串是否为有效的JSON对象格式
     *
     * @param jsonString 待验证的字符串
     * @return true表示有效，false表示无效
     */
    public boolean isValidJsonObject(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            return jsonObject != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证字符串是否为有效的JSON数组格式
     *
     * @param jsonString 待验证的字符串
     * @return true表示有效，false表示无效
     */
    public boolean isValidJsonArray(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }
        try {
            JSONArray jsonArray = JSON.parseArray(jsonString);
            return jsonArray != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 深度克隆对象（通过JSON序列化和反序列化）
     *
     * @param object 待克隆的对象
     * @param clazz  目标类型
     * @param <T>    泛型类型
     * @return 克隆后的对象
     */
    public <T> T deepClone(Object object, Class<T> clazz) {
        if (object == null || clazz == null) {
            return null;
        }
        try {
            String jsonString = JSON.toJSONString(object);
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 合并两个JSONObject
     *
     * @param target 目标JSONObject
     * @param source 源JSONObject
     * @return 合并后的JSONObject
     */
    public JSONObject mergeJsonObject(JSONObject target, JSONObject source) {
        if (target == null && source == null) {
            return null;
        }
        if (target == null) {
            return source.clone();
        }
        if (source == null) {
            return target.clone();
        }

        JSONObject result = target.clone();
        for (String key : source.keySet()) {
            result.put(key, source.get(key));
        }
        return result;
    }

    /**
     * 从JSONObject中安全获取字符串值
     *
     * @param jsonObject   JSONObject对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 字符串值
     */
    public String getString(JSONObject jsonObject, String key, String defaultValue) {
        if (jsonObject == null || key == null) {
            return defaultValue;
        }
        try {
            return jsonObject.getString(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 从JSONObject中安全获取整数值
     *
     * @param jsonObject   JSONObject对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 整数值
     */
    public Integer getInteger(JSONObject jsonObject, String key, Integer defaultValue) {
        if (jsonObject == null || key == null) {
            return defaultValue;
        }
        try {
            return jsonObject.getInteger(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 从JSONObject中安全获取长整数值
     *
     * @param jsonObject   JSONObject对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 长整数值
     */
    public Long getLong(JSONObject jsonObject, String key, Long defaultValue) {
        if (jsonObject == null || key == null) {
            return defaultValue;
        }
        try {
            return jsonObject.getLong(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 从JSONObject中安全获取布尔值
     *
     * @param jsonObject   JSONObject对象
     * @param key          键名
     * @param defaultValue 默认值
     * @return 布尔值
     */
    public Boolean getBoolean(JSONObject jsonObject, String key, Boolean defaultValue) {
        if (jsonObject == null || key == null) {
            return defaultValue;
        }
        try {
            return jsonObject.getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}