package com.tjut.zjone.common.enums;

import com.tjut.zjone.common.convention.errorcode.IErrorCode;

public enum UserErrorCodeEnum implements IErrorCode {

    USER_TOKEN_FAIL("A000201", "用户token验证失败"),
    USER_REGISTER_FAIL("A000202", "用户注册失败"),
    USER_PUT_REG_FAIL("A000203", "提交用户信息失败"),
    USER_NULL("B000200","用户不存在"),
    USER_NAME_EXISTS("B000201","用户名已存在"),
    USER_EXISTS("B000202", "用户已登陆"),
    USER_SAVE_FAIL("B000203", "用户记录新增失败"),
    USER_PARAM_NULL("B000204", "账号或密码不能为空"),
    USER_NAME_LENGTH_ERROR("B000205", "用户账号不能低于4位"),
    USER_PASSWORD_LENGTH_ERROR("B000206", "密码不能低于6位"),
    USER_NAME_PATTERN_ERROR("B000207", "用户账号不能包含特殊字符"),
    USER_NAME_REPETITION("B000208", "用户名重复"),
    USER_PASSWORD_ERROR("B000209", "密码错误，请重试"),
    STUDENT_ID_ERROR("B000210", "学号格式错误"),
    STUDENT_NAME_ERROR("B000210", "姓名格式错误"),
    STUDENT_QQ_ERROR("B000210", "qq格式错误"),
    STUDENT_PHONE_ERROR("B000210", "手机号格式错误"),
    STUDENT_NO_AUTH("B000211", "学生没有修改权限"),

            ;


    public final String code;
    public final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
