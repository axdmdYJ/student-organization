package com.tjut.zjone.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
public class MapUtils {

    /**
     * 创建一个Map，并初始化键值对
     *
     * @param k   第一个键
     * @param v   第一个值
     * @param kvs 其他键值对（可变参数）
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @return 初始化后的Map
     */
    public static <K, V> Map<K, V> create(K k, V v, Object... kvs) {
        // 使用Guava库的Maps.newHashMapWithExpectedSize方法创建HashMap
        Map<K, V> map = Maps.newHashMapWithExpectedSize(kvs.length + 1);
        map.put(k, v);

        // 遍历可变参数，每两个参数为一组键值对，加入到Map中
        for (int i = 0; i < kvs.length; i += 2) {
            map.put((K) kvs[i], (V) kvs[i + 1]);
        }

        return map;
    }

    /**
     * 将集合转换为Map
     *
     * @param list 需要转换的集合
     * @param key  提取键的Function
     * @param val  提取值的Function
     * @param <T>  集合元素的类型
     * @param <K>  键的类型
     * @param <V>  值的类型
     * @return 转换后的Map
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> list, Function<T, K> key, Function<T, V> val) {
        // 如果集合为空，返回空的HashMap
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMapWithExpectedSize(0);
        }

        // 使用Java 8的Stream API将集合转换为Map
        return list.stream().collect(Collectors.toMap(key, val));
    }
}

