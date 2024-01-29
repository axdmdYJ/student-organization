package com.tjut.zjone.controller;


import com.tjut.zjone.common.convention.result.Result;
import com.tjut.zjone.common.convention.result.Results;
import com.tjut.zjone.dto.req.UserLoginReqDTO;
import com.tjut.zjone.dto.req.UserPutRegReqDTO;
import com.tjut.zjone.dto.req.UserRegisterReqDTO;
import com.tjut.zjone.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login/c")
    public Result<String> login(@RequestBody UserLoginReqDTO requestParam){
        String token = userService.userLogin(requestParam.getUsername(), requestParam.getPassword());
        return Results.success(token);
    }

    @PutMapping("/registration-information")
    public Result<Void> putInformation(@RequestBody UserPutRegReqDTO requestParam){
        userService.putInformation(requestParam);
        return Results.success();
    }
}
