package com.tjut.zjone.dto.resp;


import lombok.Data;

@Data
public class UserLoginRespDTO {

    /**
     * 验证码
     */
    private String token;
}
