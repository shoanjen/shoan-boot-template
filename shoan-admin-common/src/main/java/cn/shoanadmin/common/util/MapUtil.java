package cn.shoanadmin.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@UtilityClass
@Slf4j
public class MapUtil {

    /**
     * map转为[key,value,key,value]
     * @param map
     * @return
     */
    public List<String> toKeyValueList(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.add(Objects.toString(entry.getKey(), ""));
            result.add(Objects.toString(entry.getValue(), ""));
        }
        return result;
    }

    /**
     * map转为key:value:key:value
     * @param map
     * @param separator
     * @return
     */
    public String mapToStringSeparated(Map<String, String> map, String separator) {
        if (map == null || map.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return String.join(separator, toKeyValueList(map));
    }

    public Map<String, String> stringToMapSeparated(String str, String separator) {

        if (StringUtils.isEmpty(str)) {
            return new LinkedHashMap<>();
        }

        String[] splitStr = str.split(separator);

        Map<String, String> resultMap = new LinkedHashMap<>(splitStr.length / 2);
        if (splitStr.length % 2 != 0) {
           log.warn("stringToMapSeparated style error");
           return resultMap;
        }

        for (int i = 0; i < splitStr.length; i += 2) {
            String key = splitStr[i];
            String value = splitStr[i + 1]; // value 可以为空字符串，但不能为 null
            resultMap.put(key, value);
        }
        return resultMap;
    }

}
