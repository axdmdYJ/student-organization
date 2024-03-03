//package com.tjut.zjone.common.biz.user;
//
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson2.JSON;
//import com.google.common.collect.Lists;
//import com.tjut.zjone.common.convention.exception.ClientException;
//import com.tjut.zjone.common.convention.result.Results;
//import com.tjut.zjone.common.enums.UserErrorCodeEnum;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//
///**
// * 用户信息传输过滤器
// *
// */
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class UserTransmitFilter implements Filter {
//    public final StringRedisTemplate stringRedisTemplate;
//
//    private final static List<String> IGNORE_URI = Lists.newArrayList(
//        "/register/c",
//            "/login/c",
//            "/login/b"
//    );
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        String requestURI = httpServletRequest.getRequestURI();
//        if (!IGNORE_URI.contains(requestURI)){
//                String token = httpServletRequest.getHeader("token");
//                //woken不能为空
//                if (!StrUtil.isNotBlank(token)){
//                    returnJson(servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
//                    return;
//                }
//                Object userInfoJsonStr = null;
//                try {
//                    userInfoJsonStr = stringRedisTemplate.opsForValue().get("login_" + token);
//                    if (userInfoJsonStr==null){
//                        returnJson(servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
//                        return;
//                    }
//                } catch (Exception e) {
//                    returnJson(servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
//                    return;
//                }
//                    UserInfo userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfo.class);
//                    UserContext.setUser(userInfoDTO);
//        }
//        try {
//         filterChain.doFilter(servletRequest, servletResponse);
//        } finally {
//            UserContext.removeUser();
//        }
//    }
//    private void returnJson(ServletResponse response, String json) {
//        PrintWriter writer = null;
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json; charset=utf-8");
//        try {
//            writer = response.getWriter();
//            writer.print(json);
//        } catch (IOException e) {
//            log.error("response error", e);
//        } finally {
//            if (writer != null)
//                writer.close();
//        }
//    }
//
//}