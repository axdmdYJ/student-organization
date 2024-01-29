package com.tjut.zjone.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tjut.zjone.common.convention.exception.ClientException;
import com.tjut.zjone.common.convention.exception.ServiceException;
import com.tjut.zjone.common.enums.UserErrorCodeEnum;
import com.tjut.zjone.dao.entity.UserDO;
import com.tjut.zjone.dto.req.UserRegisterReqDTO;
import com.tjut.zjone.service.UserService;
import com.tjut.zjone.dao.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @description 针对表【t_user】的数据库操作Service实现
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
    implements UserService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String SALT = "salt";
    @Override
    public void userRegister(String username, String password) {
        //1. 格式校验
        formatCheck(username, password);
        // 1.3 账户不能重复
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        long count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_REPETITION);
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 插入数据
        UserDO user = UserDO.builder()
                .username(username)
                .password(encryptPassword)
                .build();
        try {
            baseMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(UserErrorCodeEnum.USER_REGISTER_FAIL);
        }
    }

    @Override
    public String userLogin(String username, String password) {
        //1. 格式校验
        formatCheck(username, password);
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 查询用户是否存在
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0);
        UserDO user = baseMapper.selectOne(queryWrapper);
        // 3.1 用户不存在
        if (user == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        // 3.2 存在则判断密码是否正确
        if (!encryptPassword.equals(user.getPassword())){
            throw new ClientException(UserErrorCodeEnum.USER_PASSWORD_ERROR);
        }
        //3. 缓存处理，返回token
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_"+username, token, JSON.toJSONString(user));
        stringRedisTemplate.expire("login_"+username,30L, TimeUnit.MINUTES);
        return token;
    }

    private static void formatCheck(String username, String password) {
        // 1. 校验
        // 1.1 长度校验
        if (StrUtil.isEmpty(username) || StrUtil.isEmpty(password)) {
            throw new ClientException(UserErrorCodeEnum.USER_PARAM_NULL);
        }
        if (username.length() < 4) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_LENGTH_ERROR);
        }
        if (password.length() < 6) {
            throw new ClientException(UserErrorCodeEnum.USER_PASSWORD_LENGTH_ERROR);
        }
        // 1.2 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(username);
        if (matcher.find()) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_PATTERN_ERROR);
        }
    }
}




