package com.tjut.zjone.controller;


import com.tjut.zjone.common.convention.result.Result;
import com.tjut.zjone.common.convention.result.Results;
import com.tjut.zjone.dto.req.*;
import com.tjut.zjone.dto.resp.UserGetInfoRespDTO;
import com.tjut.zjone.dto.resp.UserLoginRespDTO;
import com.tjut.zjone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 控制层
 */

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    /**
     * 注册
     */
    @PostMapping("/register/c")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam){
        userService.userRegister(requestParam.getUsername(), requestParam.getPassword());
        return  Results.success();
    }

    /**
     * 学生登陆
     * @param requestParam 学生登陆参数
     */
    @PostMapping("/login/c")
    public Result<UserLoginRespDTO> userLogin(@RequestBody UserLoginReqDTO requestParam){
        UserLoginRespDTO userLoginRespDTO = userService.userLogin(requestParam.getUsername(), requestParam.getPassword());
        return Results.success(userLoginRespDTO);
    }

    /**
     * 管理员登陆
     * @param requestParam 管理员登陆参数
     */
    @PostMapping("/login/b")
    public Result<UserLoginRespDTO> AdminLogin(@RequestBody UserLoginReqDTO requestParam){
        UserLoginRespDTO userLoginRespDTO = userService.userLogin(requestParam.getUsername(), requestParam.getPassword());
        return Results.success(userLoginRespDTO);
    }


    @PutMapping("/registration-information/admin")
    public Result<Void> adminUpdate(@RequestBody AdminUpdateDTO requestParam){
        userService.updateStudent(requestParam);
        return Results.success();
    }
    /**
     * 学生报名信息提交
     * @param requestParam 学生报名提交参数
     */
    @PutMapping("/registration-information")
    public Result<Void> userPut(@RequestBody UserPutRegReqDTO requestParam){
        userService.putInformation(requestParam);
        return Results.success();
    }

    @GetMapping("/registration-information")
    public Result<UserGetInfoRespDTO> userGet(){
        return Results.success(userService.getInfo());
    }

    @PostMapping("/password/reset")
    public Result<Void>  adminReset(@RequestBody UserPwdResetReqDTO requestParam){
        userService.adminRest(requestParam);
        return Results.success();
    }
}
