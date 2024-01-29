package com.tjut.zjone.common.enums;

public enum RoleEnum {
    // 社长
    STUDENT(0),
    ADMIN(1),
    ;
    public final Integer role;

    RoleEnum(Integer role) {
        this.role = role;
    }
}
