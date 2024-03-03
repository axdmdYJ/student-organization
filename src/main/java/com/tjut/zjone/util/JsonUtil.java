package com.tjut.zjone.util;

import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtil {

    // ObjectMapper是Jackson库的核心类，用于实现JSON和Java对象的相互转换
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    // 将JSON字符串转换为指定类型的Java对象
    public static <T> T toObj(String str, Class<T> clz) {
        try {
            return jsonMapper.readValue(str, clz);
        } catch (Exception e) {
            // 异常处理，将异常包装为UnsupportedOperationException并抛出
            throw new UnsupportedOperationException(e);
        }
    }

    // 将Java对象转换为JSON字符串
    public static <T> String toStr(T t) {
        try {
            return jsonMapper.writeValueAsString(t);
        } catch (Exception e) {
            // 异常处理，将异常包装为UnsupportedOperationException并抛出
            throw new UnsupportedOperationException(e);
        }
    }
}
