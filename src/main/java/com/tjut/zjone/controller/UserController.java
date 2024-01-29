package com.tjut.zjone.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tjut.zjone.common.convention.result.Result;
import com.tjut.zjone.common.convention.result.Results;
import com.tjut.zjone.dao.entity.UserDO;
import com.tjut.zjone.dto.req.*;
import com.tjut.zjone.dto.resp.AdminGetInfoRespDTO;
import com.tjut.zjone.dto.resp.UserGetInfoRespDTO;
import com.tjut.zjone.dto.resp.UserLoginRespDTO;
import com.tjut.zjone.dto.resp.UserPageRespDTO;
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
     * 学生注册
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
    public Result<UserLoginRespDTO> adminLogin(@RequestBody UserLoginReqDTO requestParam){
        UserLoginRespDTO userLoginRespDTO = userService.userLogin(requestParam.getUsername(), requestParam.getPassword());
        return Results.success(userLoginRespDTO);
    }

    @GetMapping("/user/info")
    public Result<AdminGetInfoRespDTO> adminGet(){
        return Results.success(userService.adminGetInfo());
    }

    /**
     * 管理员修改学生信息
     * @param requestParam 学生新信息参数
     */
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

    /**
     * 学生获得自己信息
     */
    @GetMapping("/registration-information")
    public Result<UserGetInfoRespDTO> userGet(){
        return Results.success(userService.getInfo());
    }

    /**
     * 获取学生报名信息
     * @param pageNum 第几页
     * @param pageSize 每页容量
     * @param keyword 关键字
     */
    @GetMapping("/registration-information/list")
    public Result<IPage<UserPageRespDTO>> userPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ){
        return userService.userPage(pageNum, pageSize, keyword);
    }
    /**
     * 管理员重置用户密码
     * @param requestParam 学生重置信息参数
     */
    @PostMapping("/password/reset")
    public Result<Void>  adminReset(@RequestBody UserPwdResetReqDTO requestParam){
        userService.adminRest(requestParam);
        return Results.success();
    }
}
