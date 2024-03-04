package com.tjut.zjone.common.biz.user;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.tjut.zjone.util.JsonUtil;
import com.tjut.zjone.util.MapUtils;
import com.tjut.zjone.util.SelfTraceIdGenerator;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 使用jwt来存储用户token，则不需要后端来存储session了
 *
 */
@Slf4j
@Component
public class UserSessionHelper  {
    @Component
    @Data
    @ConfigurationProperties("sipc.jwt")
    public static class JwtProperties {
        /**
         * 签发人
         */
        private String issuer;
        /**
         * 密钥
         */
        private String secret;
        /**
         * 有效期，毫秒时间戳
         */
        private Long expire;
    }

    private final JwtProperties jwtProperties;

    @Resource
    StringRedisTemplate stringRedisTemplate;
    private Algorithm algorithm;
    private JWTVerifier verifier;

    public UserSessionHelper(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        verifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
    }

    public String genSession(String username) {
        // 1.生成jwt格式的会话，内部持有有效期，用户信息
        String session = JsonUtil.toStr(MapUtils.create("s", SelfTraceIdGenerator.generate(), "u", username));
        String token = JWT.create().withIssuer(jwtProperties.getIssuer()).withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
                .withPayload(session)
                .sign(algorithm);
        // 2.使用jwt生成的token时，后端可以不存储这个session信息, 完全依赖jwt的信息
        // 但是需要考虑到用户登出，需要主动失效这个token，而jwt本身无状态，所以再这里的redis做一个简单的token -> userId的缓存，用于双重判定
        stringRedisTemplate.opsForValue().set(token, username);
        stringRedisTemplate.expire(token, jwtProperties.getExpire() / 1000, TimeUnit.MINUTES);
        return token;
    }

    public void removeSession(String session) {
        stringRedisTemplate.delete(session);
    }

    /**
     * 根据会话获取用户信息
     *
     * @param session
     * @return
     */
    public String getUserNameBySession(String session) {
        // jwt的校验方式，如果token非法或者过期，则直接验签失败
        try {
            DecodedJWT decodedJWT = verifier.verify(session);
            String pay = new String(Base64Utils.decodeFromString(decodedJWT.getPayload()));
            // jwt验证通过，获取对应的username
            String username = String.valueOf(JsonUtil.toObj(pay, HashMap.class).get("u"));

            // 从redis中获取username，解决用户登出，后台失效jwt token的问题
            String user = stringRedisTemplate.opsForValue().get(session);
            if (user == null || !Objects.equals(username, user)) {
                return null;
            }
            return user;
        } catch (Exception e) {
            log.info("jwt token校验失败! token: {}, msg: {}", session, e.getMessage());
            return null;
        }
    }
}
