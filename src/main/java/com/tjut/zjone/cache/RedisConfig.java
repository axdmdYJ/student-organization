package com.tjut.zjone.cache;

import com.tjut.zjone.cache.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * 自定义RedisTemplate
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 设置序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(stringRedisSerializer);

        // 其他配置，如Hash等的序列化器设置...

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisClient redisClient(RedisTemplate<String, String> redisTemplate) {
        RedisClient.register(redisTemplate);
        return new RedisClient();
    }
}
