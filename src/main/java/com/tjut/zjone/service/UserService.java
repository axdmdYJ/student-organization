package com.tjut.zjone.service;

import com.tjut.zjone.dao.entity.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tjut.zjone.dto.req.UserRegisterReqDTO;

/**
* @description 针对表【t_user】的数据库操作Service
*/
public interface UserService extends IService<UserDO> {

    void userRegister(String username, String password);

    String userLogin(String username, String password);
}
