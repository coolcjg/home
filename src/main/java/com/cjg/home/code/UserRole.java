package com.cjg.home.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    /*
    권한기능은 사용하지 않음
     */
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;
}
