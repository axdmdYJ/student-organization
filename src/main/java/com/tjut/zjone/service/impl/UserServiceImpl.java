package com.tjut.zjone.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tjut.zjone.common.biz.user.UserContext;
import com.tjut.zjone.common.convention.exception.ClientException;
import com.tjut.zjone.common.convention.exception.ServiceException;
import com.tjut.zjone.common.enums.RoleEnum;
import com.tjut.zjone.common.enums.UserErrorCodeEnum;
import com.tjut.zjone.dao.entity.UserDO;
import com.tjut.zjone.dto.req.AdminUpdateDTO;
import com.tjut.zjone.dto.req.UserPutRegReqDTO;
import com.tjut.zjone.dto.req.UserPwdResetReqDTO;
import com.tjut.zjone.dto.resp.UserGetInfoRespDTO;
import com.tjut.zjone.dto.resp.UserLoginRespDTO;
import com.tjut.zjone.service.UserService;
import com.tjut.zjone.dao.mapper.UserMapper;
import com.tjut.zjone.util.FormatVerifyUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    public UserLoginRespDTO userLogin(String username, String password) {
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
        stringRedisTemplate.opsForValue().set("login_"+token , JSON.toJSONString(user));
        stringRedisTemplate.expire("login_"+token,30L, TimeUnit.MINUTES);
        UserLoginRespDTO userLoginRespDTO = new UserLoginRespDTO(token);
        return userLoginRespDTO;
    }

    @Override
    public void putInformation(UserPutRegReqDTO requestParam) {
        // 1.验证格式,格式错误，抛出对应异常
        UserReqInformationFormatVerify(requestParam);
        // 2. 获取学生信息
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, UserContext.getUsername());
        UserDO user = UserDO.builder()
                .studentID(requestParam.getStudentID())
                .qq(requestParam.getQq())
                .major(requestParam.getMajor())
                .name(requestParam.getName())
                .className(requestParam.getClassName())
                .isDispensing(requestParam.getIsDispensing())
                .phone(requestParam.getPhone())
                .wills(JSON.toJSONString(requestParam.getWills()))
                .build();
        try {
            baseMapper.update(user,queryWrapper);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(UserErrorCodeEnum.USER_PUT_REG_FAIL);
        }
    }

    @Override
    public UserGetInfoRespDTO getInfo() {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, UserContext.getUsername());
        UserDO user = baseMapper.selectOne(queryWrapper);

        return BeanUtil.copyProperties(user, UserGetInfoRespDTO.class);
    }

    @Override
    public void updateStudent(AdminUpdateDTO requestParam) {
        //1. 鉴权
        if (UserContext.getRole() != RoleEnum.ADMIN.role){
            throw new ClientException(UserErrorCodeEnum.STUDENT_NO_AUTH);
        }
        //2. 验证格式,格式错误，抛出对应异常
        AdminReqInformationFormatVerify(requestParam);
        //3. 通过学号获取学生信息
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getStudentID, requestParam.getStudentID());
        UserDO user = UserDO.builder()
                .studentID(requestParam.getStudentID())
                .qq(requestParam.getQq())
                .major(requestParam.getMajor())
                .name(requestParam.getName())
                .className(requestParam.getClassName())
                .isDispensing(requestParam.getIsDispensing())
                .phone(requestParam.getPhone())
                .wills(JSON.toJSONString(requestParam.getWills()))
                .build();
        try {
            baseMapper.update(user,queryWrapper);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(UserErrorCodeEnum.USER_PUT_REG_FAIL);
        }
    }

    @Override
    public void adminRest(UserPwdResetReqDTO requestParam) {
        // 1. 用户权限校验
        if (UserContext.getRole() != RoleEnum.ADMIN.role){
            throw new ClientException(UserErrorCodeEnum.STUDENT_NO_AUTH);
        }
        // 2. 对新密码进行验证
        // 2.1 长度校验
        if (StrUtil.isEmpty(requestParam.getNewPassword())) {
            throw new ClientException(UserErrorCodeEnum.USER_PARAM_NULL);
        }

        if (requestParam.getNewPassword().length() < 6) {
            throw new ClientException(UserErrorCodeEnum.USER_PASSWORD_LENGTH_ERROR);
        }
        //2.2 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + requestParam.getNewPassword()).getBytes());
        // 3. 根据学号查询用户
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getStudentID, requestParam.getStudentID());
        UserDO user = UserDO.builder()
                .password(encryptPassword)
                .build();
        try {
            baseMapper.update(user,queryWrapper);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(UserErrorCodeEnum.USER_PUT_REG_FAIL);
        }
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
    private static void UserReqInformationFormatVerify(UserPutRegReqDTO requestParam){
        if (!FormatVerifyUtil.isValidStudentID(requestParam.getStudentID())){
            throw new ClientException(UserErrorCodeEnum.STUDENT_ID_ERROR);
        }
        if (!FormatVerifyUtil.isValidName(requestParam.getName())){
            throw new ClientException(UserErrorCodeEnum.STUDENT_NAME_ERROR);
        }
        if (!FormatVerifyUtil.isValidQQ(requestParam.getQq())){
            throw new ClientException(UserErrorCodeEnum.STUDENT_QQ_ERROR);
        }
        if (!FormatVerifyUtil.isValidPhoneNumber(requestParam.getPhone())){
            throw new ClientException(UserErrorCodeEnum.STUDENT_PHONE_ERROR);
        }
    }

    private static void AdminReqInformationFormatVerify(AdminUpdateDTO requestParam){
        if (!FormatVerifyUtil.isValidStudentID(requestParam.getStudentID())){
            throw new ClientException(UserErrorCodeEnum.STUDENT_ID_ERROR);
        }
        if (!FormatVerifyUtil.isValidName(requestParam.getName())){
            throw new ClientException(UserErrorCodeEnum.STUDENT_NAME_ERROR);
        }
        if (!FormatVerifyUtil.isValidQQ(requestParam.getQq())){
            throw new ClientException(UserErrorCodeEnum.STUDENT_QQ_ERROR);
        }
        if (!FormatVerifyUtil.isValidPhoneNumber(requestParam.getPhone())){
            throw new ClientException(UserErrorCodeEnum.STUDENT_PHONE_ERROR);
        }
    }
}




