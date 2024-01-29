package com.tjut.zjone.controller;


import com.tjut.zjone.common.convention.result.Result;
import com.tjut.zjone.common.convention.result.Results;
import com.tjut.zjone.dto.req.UserLoginReqDTO;
import com.tjut.zjone.dto.req.UserPutRegReqDTO;
import com.tjut.zjone.dto.req.UserRegisterReqDTO;
import com.tjut.zjone.dto.resp.UserGetInfoRespDTO;
import com.tjut.zjone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学生控制层
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
     * 登陆
     * @param requestParam 学生登陆参数
     */
    @PostMapping("/login/c")
    public Result<String> login(@RequestBody UserLoginReqDTO requestParam){
        String token = userService.userLogin(requestParam.getUsername(), requestParam.getPassword());
        return Results.success(token);
    }

    @PutMapping("/registration-information")
    public Result<Void> put(@RequestBody UserPutRegReqDTO requestParam){
        userService.putInformation(requestParam);
        return Results.success();
    }

    @GetMapping("/registration-information")
    public Result<UserGetInfoRespDTO> get(){
        return Results.success(userService.getInfo());
    }
}
