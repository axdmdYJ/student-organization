package com.tjut.zjone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tjut.zjone.dao.mapper")
public class StudentOrganizationApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentOrganizationApplication.class, args);
    }

}
