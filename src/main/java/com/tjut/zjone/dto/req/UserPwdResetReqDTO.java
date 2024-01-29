package com.tjut.zjone.dto.req;


import lombok.Data;

@Data
public class UserPwdResetReqDTO {

    /**
     * 学生学号
     */
    private String studentID;

    /**
     * 新密码
     */
    private String newPassword;
}
