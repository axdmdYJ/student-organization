package com.tjut.zjone.common.biz.user;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tjut.zjone.dao.entity.UserDO;
import com.tjut.zjone.dao.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.utils.JavaUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Jwt拦截器
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    private final UserSessionHelper userSessionHelper;

    private final UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (!(handler instanceof HandlerMethod)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String username = userSessionHelper.getUserNameBySession(token);

        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        UserContext.setUser(BeanUtil.toBean(userDO, UserInfo.class));
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
