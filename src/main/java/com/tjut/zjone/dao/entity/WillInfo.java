package com.tjut.zjone.dao.entity;


import lombok.Data;

/**
 * 学生志愿信息
 */
@Data
public class WillInfo {
    /**
     * 组织
     */
    private String organization;

    /**
     * 部门
     */
    private String department;

    /**
     * 理由
     */
    private String reason;
}
