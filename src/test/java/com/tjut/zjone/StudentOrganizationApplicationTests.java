package com.tjut.zjone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.tjut.zjone.util.FormatVerifyUtil.*;

@SpringBootTest
class StudentOrganizationApplicationTests {

    @Test
    void formatTest() {
        System.out.println(isValidStudentID("12345678")); // true
        System.out.println(isValidName("张三")); // true
        System.out.println(isValidName("John")); // false
        System.out.println(isValidQQ("12345")); // false
        System.out.println(isValidPhoneNumber("12345678901")); // true
    }

}
