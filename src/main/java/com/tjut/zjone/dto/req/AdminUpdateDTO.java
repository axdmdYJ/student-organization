package com.tjut.zjone.dto.req;

import com.tjut.zjone.dao.entity.WillInfo;
import lombok.Data;

import java.util.List;

/**
 * 管理元修改学生信息参数
 */
@Data
public class AdminUpdateDTO {
    /**
     * 学生学号
     */
    private String studentID;

    /**
     * 真实姓名
     */
    private String name;
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
    private List<WillInfo> wills;

    /**
     * 同意调剂 0:同意 1:不同意
     */
    private Boolean isDispensing;
}
