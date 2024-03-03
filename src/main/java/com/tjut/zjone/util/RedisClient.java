package com.tjut.zjone.util;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Redis 客户端工具类
 */
public class RedisClient {

    private static final Charset CODE = StandardCharsets.UTF_8;
    private static final String KEY_PREFIX = "sipc_";
    private static RedisTemplate<String, String> template;

    /**
     * 注册 RedisTemplate
     *
     * @param template RedisTemplate 实例
     */

    public static void register(RedisTemplate<String, String> template) {
        RedisClient.template = template;
    }

    /**
     * 检查参数是否为 null
     *
     * @param args 要检查的参数
     * @throws IllegalArgumentException 如果参数为 null
     */
    public static void nullCheck(Object... args) {
        for (Object obj : args) {
            if (obj == null) {
                throw new IllegalArgumentException("Redis argument can not be null!");
            }
        }
    }

    /**
     * 生成缓存键的字节数组
     *
     * @param key 键
     * @return 返回字节数组
     */
    public static byte[] keyBytes(String key) {
        nullCheck(key);
        key = KEY_PREFIX + key;
        return key.getBytes(CODE);
    }

    /**
     * 查询缓存
     *
     * @param key 缓存键
     * @return 缓存值的字符串表示形式
     */
    public static String getStr(String key) {
        return template.execute((RedisCallback<String>) con -> {
            byte[] val = con.get(keyBytes(key)); // 通过连接获取缓存值的字节数组
            return val == null ? null : new String(val, CODE); // 将字节数组转换为字符串
        });
    }


    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public static void del(String key) {
        template.execute((RedisCallback<Long>) con -> con.del(keyBytes(key)));
    }

    /**
     * 缓存值的序列化处理
     *
     * @param val 缓存值
     * @param <T> 缓存值的类型
     * @return 缓存值的字节数组
     */
    public static <T> byte[] valBytes(T val) {
        if (val instanceof String) {
            // 如果缓存值是字符串，直接获取其字节数组表示
            return ((String) val).getBytes(CODE);
        } else {
            // 如果不是 String 类型，将其转换为 JSON 字符串再转为字节数组
            return JsonUtil.toStr(val).getBytes(CODE);
        }
    }


    /**
     * 带过期时间的缓存写入
     *
     * @param key
     * @param value
     * @param expire s为单位
     * @return
     */
    public static Boolean setStrWithExpire(String key, String value, Long expire) {
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.setEx(keyBytes(key), expire, valBytes(value));
            }
        });
    }


}