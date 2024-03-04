package com.tjut.zjone.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tjut.zjone.common.biz.user.UserContext;
import com.tjut.zjone.common.biz.user.UserSessionHelper;
import com.tjut.zjone.common.convention.exception.ClientException;
import com.tjut.zjone.common.convention.exception.ServiceException;
import com.tjut.zjone.common.convention.result.Result;
import com.tjut.zjone.common.convention.result.Results;
import com.tjut.zjone.common.enums.RoleEnum;
import com.tjut.zjone.common.enums.UserErrorCodeEnum;
import com.tjut.zjone.dao.entity.UserDO;
import com.tjut.zjone.dto.req.AdminUpdateDTO;
import com.tjut.zjone.dto.req.UserPutRegReqDTO;
import com.tjut.zjone.dto.req.UserPwdResetReqDTO;
import com.tjut.zjone.dto.resp.AdminGetInfoRespDTO;
import com.tjut.zjone.dto.resp.UserGetInfoRespDTO;
import com.tjut.zjone.dto.resp.UserLoginRespDTO;
import com.tjut.zjone.dto.resp.UserPageRespDTO;
import com.tjut.zjone.mq.producer.StudentPutInfoProducer;
import com.tjut.zjone.service.UserService;
import com.tjut.zjone.dao.mapper.UserMapper;
import com.tjut.zjone.util.FormatVerifyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tjut.zjone.common.constant.RoleConstant.B_ADMIN;
import static com.tjut.zjone.common.constant.RoleConstant.C_STUDENT;

/**
 * @description 针对表【t_user】的数据库操作Service实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final StringRedisTemplate stringRedisTemplate;

    private final StudentPutInfoProducer studentPutInfoProducer;
    private final RabbitTemplate rabbitTemplate;
    private final UserSessionHelper userSessionHelper;

    private static final String SALT = "salt";

    @Override
    public void userRegister(String username, String password) {
        // 1. 格式校验
        formatCheck(username, password);
        // 1.3 账户不能重复
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        long count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_REPETITION);
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 插入数据
        UserDO user = UserDO.builder().username(username).password(encryptPassword).build();
        try {
            baseMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(UserErrorCodeEnum.USER_REGISTER_FAIL);
        }
    }

    @Override
    public UserLoginRespDTO userLogin(String username, String password) {
        // 1. 格式校验
        formatCheck(username, password);
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 查询用户是否存在
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO user = baseMapper.selectOne(queryWrapper);
        // 3.1 用户不存在
        if (user == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        // 3.2 存在则判断密码是否正确
        if (!encryptPassword.equals(user.getPassword())) {
            throw new ClientException(UserErrorCodeEnum.USER_PASSWORD_ERROR);
        }
        // 4. 缓存处理，返回token
//        String token = UUID.randomUUID().toString();
//        stringRedisTemplate.opsForValue().set("login_" + token, JSON.toJSONString(user));
//        stringRedisTemplate.expire("login_" + token, 30L, TimeUnit.MINUTES);
        String token = userSessionHelper.genSession(username);
        UserLoginRespDTO userLoginRespDTO = new UserLoginRespDTO(token);
        return userLoginRespDTO;
    }

    @Override
    public void putInformation(UserPutRegReqDTO requestParam) {
        // 1.验证格式,格式错误，抛出对应异常
        UserReqInformationFormatVerify(requestParam);
        // 2. 获取学生信息
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, UserContext.getUsername());
        UserDO userDO = UserDO.builder().username(UserContext.getUsername()).major(requestParam.getMajor()).name(requestParam.getName()).phone(requestParam.getPhone()).isDispensing(requestParam.getIsDispensing()).wills(JSON.toJSONString(requestParam.getWills())).qq(requestParam.getQq()).className(requestParam.getClassName()).studentID(requestParam.getStudentID()).build();
        studentPutInfoProducer.send(userDO);
        return;
//        Map<Object, Object> map = new HashMap<>();
//        map.put("username", UserContext.getUsername());
//        map.put("studentID", requestParam.getStudentID());
//        map.put("qq", requestParam.getQq());
//        map.put("major", requestParam.getMajor());
//        map.put("name", requestParam.getName());
//        map.put("className", requestParam.getClassName());
//        map.put("isDispensing", requestParam.getIsDispensing().toString());
//        map.put("phone",requestParam.getPhone());
//        map.put("wills",JSON.toJSONString(requestParam.getWills()));
//        UserDO user = BeanUtil.fillBeanWithMap(map, new UserDO(), true);
//        stringRedisTemplate.opsForValue().set("login_"+UserContext.getUsername(), JSON.toJSONString(user),30L,TimeUnit.MINUTES);
        // XGROUP CREATE stream.students g1 0 MKSTREAM
//        stringRedisTemplate.opsForStream().add("stream.students", map);
    }

    //=============================Redis异步调用========================
//    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();
//
//    //在当前类初始化完毕后执行
//    @PostConstruct
//    private void init() {
//        //开启异步线程调用任务
//        SECKILL_ORDER_EXECUTOR.submit(new PutInfoHandler());
//    }
//
//    private class PutInfoHandler implements Runnable {
//
//        private static final String QUEUE_NAME = "stream.students";
//
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    // 1.获取消息队列中的报名信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 >
//                    //消费者名字应该配置yml文件中，这里写死
//                        List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
//                            Consumer.from("g1", "c1"),
//                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
//                            StreamOffset.create(QUEUE_NAME, ReadOffset.lastConsumed())
//                    );
//                    // 2.判断报名信息是否为空
//                    if (list == null || list.isEmpty()) {
//                        // 如果为null，说明没有消息，继续下一次循环
//                        continue;
//                    }
//
//                    // 解析数据，返回list是因为可能读取多个，这里我们读取一个
//                    // MapRecord 第一个 String 指的是 消息的ID，redis生成的
//                    MapRecord<String, Object, Object> record = list.get(0);
//                    Map<Object, Object> value = record.getValue();
//                    // 2. 获取学生信息
//                    LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
//                            .eq(UserDO::getUsername, value.get("username"));
//                    UserDO user = UserDO.builder()
//                            .studentID((String) value.get("studentID"))
//                            .qq((String) value.get("qq"))
//                            .major((String) value.get("major"))
//                            .name((String) value.get("name"))
//                            .className((String) value.get("className"))
//                            .phone((String) value.get("phone"))
//                            .isDispensing(Boolean.parseBoolean((String) value.get("isDispensing")))
//                            .wills((String) (value.get("wills")))
//                            .build();
////                    UserDO user = BeanUtil.fillBeanWithMap(value, new UserDO(), true);
////                    baseMapper.update(user,queryWrapper);
//                    baseMapper.update(user, queryWrapper);
//                    stringRedisTemplate.opsForStream().acknowledge(QUEUE_NAME, "g1", record.getId());
//                } catch (Exception e) {
//                    //处理异常情况，pending-list中的消息
//                    log.error("学生提交信息发生异常", e);
//                    handlePendingList();
//                }
//            }
//        }
//
//        //处理异常情况
//        private void handlePendingList() {
//            while (true) {
//                try {
//                    // 1.获取pending-list中的报名信息 XREADGROUP GROUP g1 c1 COUNT 1  STREAMS stream.orders 0
//                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
//                            Consumer.from("g1", "c1"),
//                            StreamReadOptions.empty().count(1),
//                            StreamOffset.create(QUEUE_NAME, ReadOffset.from("0"))
//                    );
//                    // 2.判断报名信息是否为空
//                    if (list == null || list.isEmpty()) {
//                        // 如果为null，说明没有异常消息，结束循环
//                        break;
//                    }
//                    // 解析数据
//                    // 2. 获取学生信息
//                    LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
//                            .eq(UserDO::getUsername, UserContext.getUsername());
//                    // 解析数据，返回list是因为可能读取多个，这里我们读取一个
//                    // MapRecord 第一个 String 指的是 消息的ID，redis生成的
//                    MapRecord<String, Object, Object> record = list.get(0);
//                    Map<Object, Object> value = record.getValue();
//                    UserDO user = BeanUtil.fillBeanWithMap(value, new UserDO(), true);
//                    baseMapper.update(user,queryWrapper);
//                    // 4.确认消息 XACK
//                    stringRedisTemplate.opsForStream().acknowledge(QUEUE_NAME, "g1", record.getId());
//                } catch (Exception e) {
////                    抛出异常，又会进入下一次循环，直到 pending-list 中没有异常消息。不需要递归
//                    log.error("处理pending-list异常", e);
//                    try {
//                        //防止太频繁，睡一下
//                        Thread.sleep(20);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        }
//
//    }
//

    @Override
    public UserGetInfoRespDTO getInfo() {
        String userJs = stringRedisTemplate.opsForValue().get("login_" + UserContext.getUsername());
        UserGetInfoRespDTO userGetInfoRespDTO = JSON.parseObject(userJs, UserGetInfoRespDTO.class);
        if (userGetInfoRespDTO != null) {
            return userGetInfoRespDTO;
        }
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, UserContext.getUsername());
        UserDO user = baseMapper.selectOne(queryWrapper);
        UserGetInfoRespDTO responseDTO = BeanUtil.copyProperties(user, UserGetInfoRespDTO.class);
        return responseDTO;
    }


    @Override
    public void updateStudent(AdminUpdateDTO requestParam) {
        //1. 鉴权
        if (UserContext.getRole() != RoleEnum.ADMIN.role) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_NO_AUTH);
        }
        //2. 验证格式,格式错误，抛出对应异常
        AdminReqInformationFormatVerify(requestParam);
        //3. 通过学号获取学生信息
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getStudentID, requestParam.getStudentID());
        UserDO user = UserDO.builder().studentID(requestParam.getStudentID()).qq(requestParam.getQq()).major(requestParam.getMajor()).name(requestParam.getName()).className(requestParam.getClassName()).isDispensing(requestParam.getIsDispensing()).phone(requestParam.getPhone()).wills(JSON.toJSONString(requestParam.getWills())).build();
        try {
            baseMapper.update(user, queryWrapper);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(UserErrorCodeEnum.USER_PUT_REG_FAIL);
        }
    }

    @Override
    public void adminRest(UserPwdResetReqDTO requestParam) {
        // 1. 用户权限校验
        if (UserContext.getRole() != RoleEnum.ADMIN.role) {
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
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getStudentID, requestParam.getStudentID());
        UserDO user = UserDO.builder().password(encryptPassword).build();
        try {
            baseMapper.update(user, queryWrapper);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(UserErrorCodeEnum.USER_PUT_REG_FAIL);
        }
    }

    @Override
    public AdminGetInfoRespDTO adminGetInfo() {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, UserContext.getUsername());
        UserDO user = baseMapper.selectOne(queryWrapper);
        AdminGetInfoRespDTO respDTO = new AdminGetInfoRespDTO();
        if (user.getRole().equals(RoleEnum.ADMIN.role)) {
            respDTO.setRole(B_ADMIN);
        }
        if (!user.getRole().equals(RoleEnum.ADMIN.role)) {
            respDTO.setRole(C_STUDENT);
        }
        respDTO.setUsername(UserContext.getUsername());
        return respDTO;
    }

    @Override
    public Result<IPage<UserPageRespDTO>> userPage(Integer pageNum, Integer pageSize, String keyword) {
        // 1. 用户权限校验
        if (UserContext.getRole() != RoleEnum.ADMIN.role) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_NO_AUTH);
        }
        // 2. 构建分页对象
        Page<UserDO> userPage = new Page<>(pageNum, pageSize);

        // 3. 构建查询条件
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getRole, 0).like(StringUtils.isNotBlank(keyword), UserDO::getName, keyword).or().like(StringUtils.isNotBlank(keyword), UserDO::getStudentID, keyword);

        // 3. 分页处理
        IPage<UserDO> page = baseMapper.selectPage(userPage, queryWrapper);
        return Results.success(page.convert(each -> BeanUtil.toBean(each, UserPageRespDTO.class)));
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

    private static void UserReqInformationFormatVerify(UserPutRegReqDTO requestParam) {
        if (!FormatVerifyUtil.isValidStudentID(requestParam.getStudentID())) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_ID_ERROR);
        }
        if (!FormatVerifyUtil.isValidName(requestParam.getName())) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_NAME_ERROR);
        }
        if (!FormatVerifyUtil.isValidQQ(requestParam.getQq())) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_QQ_ERROR);
        }
        if (!FormatVerifyUtil.isValidPhoneNumber(requestParam.getPhone())) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_PHONE_ERROR);
        }
    }

    private static void AdminReqInformationFormatVerify(AdminUpdateDTO requestParam) {
        if (!FormatVerifyUtil.isValidStudentID(requestParam.getStudentID())) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_ID_ERROR);
        }
        if (!FormatVerifyUtil.isValidName(requestParam.getName())) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_NAME_ERROR);
        }
        if (!FormatVerifyUtil.isValidQQ(requestParam.getQq())) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_QQ_ERROR);
        }
        if (!FormatVerifyUtil.isValidPhoneNumber(requestParam.getPhone())) {
            throw new ClientException(UserErrorCodeEnum.STUDENT_PHONE_ERROR);
        }
    }
}




