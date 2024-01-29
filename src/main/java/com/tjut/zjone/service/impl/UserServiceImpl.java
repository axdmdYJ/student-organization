package com.tjut.zjone.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @description 针对表【t_user】的数据库操作Service实现
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
    implements UserService {

    private static final String SALT = "salt";
    @Override
    public void userRegister(String username, String password) {
        // 1. 校验
        // 1.1 长度校验
        if (StrUtil.isAllEmpty(username, password)) {
            throw  new ClientException(UserErrorCodeEnum.USER_PARAM_NULL);
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
}




