package com.tjut.zjone.dto.resp;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.tjut.zjone.dao.entity.WillInfo;
import lombok.Data;

import java.util.List;

@Data
public class UserPageRespDTO {
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
    @JSONField(serialize = false)  // 忽略默认序列化
    private String wills;

    // 自定义序列化方法
    @JSONField(name = "wills")
    public List<WillInfo> getWills() {
        // 将 wlls 字符串转为 List<WillInfo> 对象
        return JSON.parseArray(wills, WillInfo.class);
    }
    /**
     * 同意调剂 0:同意 1:不同意
     */
    private Boolean isDispensing;
}
