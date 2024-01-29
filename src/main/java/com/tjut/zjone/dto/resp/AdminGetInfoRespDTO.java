package com.tjut.zjone.dto.resp;

import lombok.Data;

@Data
public class AdminGetInfoRespDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private String role;
}
