package com.tjut.zjone.dao.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName t_user
 */
@TableName(value ="t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDO implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生学号
     */
    @TableField("student_id")
    private String studentID;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * qq号
     */
    private String qq;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 专业
     */
    private String major;

    /**
     * 班级
     */
    private String className;

    /**
     * 志愿
     */
    private String wills;

    /**
     * 角色 0: 学生 1： 管理员
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer role;
    /**
     * 同意调剂 0:同意 1:不同意
     */
    private Boolean isDispensing;

    /**
     * 注销时间戳
     */
    private Long deletionTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField( fill = FieldFill.UPDATE)
    private Date updateTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}