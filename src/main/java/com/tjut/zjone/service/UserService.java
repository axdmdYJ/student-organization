package com.tjut.zjone.service;

import com.tjut.zjone.dao.entity.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tjut.zjone.dto.req.AdminUpdateDTO;
import com.tjut.zjone.dto.req.UserPutRegReqDTO;
import com.tjut.zjone.dto.req.UserPwdResetReqDTO;
import com.tjut.zjone.dto.req.UserRegisterReqDTO;
import com.tjut.zjone.dto.resp.AdminGetInfoRespDTO;
import com.tjut.zjone.dto.resp.UserGetInfoRespDTO;
import com.tjut.zjone.dto.resp.UserLoginRespDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @description 针对表【t_user】的数据库操作Service
*/
public interface UserService extends IService<UserDO> {

    /**
     * 学生注册
     * @param username 用户名
     * @param password 密码
     */
    void userRegister(String username, String password);

    /**
     * 学生登陆
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    UserLoginRespDTO userLogin(String username, String password);

    /**
     * 提交报名信息
     * @param requestParam 报名信息
     */
    void putInformation(UserPutRegReqDTO requestParam);

    UserGetInfoRespDTO getInfo();

    void updateStudent(AdminUpdateDTO requestParam);

    void adminRest(UserPwdResetReqDTO requestParam);

    AdminGetInfoRespDTO adminGetInfo();
}
