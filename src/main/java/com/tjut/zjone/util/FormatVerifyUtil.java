package com.tjut.zjone.util;

import com.tjut.zjone.common.convention.exception.ClientException;
import com.tjut.zjone.common.enums.UserErrorCodeEnum;
import com.tjut.zjone.dto.req.UserPutRegReqDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatVerifyUtil {

    // 验证学号格式（8位数字）
    public static boolean isValidStudentID(String studentID) {
        String regex = "\\d{8}";
        return validateFormat(studentID, regex);
    }

    // 验证姓名格式（只允许中文字符）
    public static boolean isValidName(String name) {
        String regex = "[\\u4e00-\\u9fa5]+";
        return validateFormat(name, regex);
    }

    // 验证QQ号格式（5位以上数字）
    public static boolean isValidQQ(String qq) {
        String regex = "\\d{5,}";
        return validateFormat(qq, regex);
    }

    // 验证手机号格式（11位数字）
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "\\d{11}";
        return validateFormat(phoneNumber, regex);
    }

    // 通用的格式验证函数
    private static boolean validateFormat(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}