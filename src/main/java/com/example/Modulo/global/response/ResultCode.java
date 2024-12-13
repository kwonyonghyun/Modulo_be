package com.example.Modulo.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultCode {

    // Auth
    SOCIAL_LOGIN_SUCCESS(200, "AU001", "소셜 로그인에 성공하였습니다."),
    TOKEN_ISSUED_SUCCESS(200, "AU002", "토큰 발급에 성공하였습니다."),
    TOKEN_REISSUED_SUCCESS(200, "AU003", "토큰 재발급에 성공하였습니다."),
    LOGOUT_SUCCESS(200, "AU004", "로그아웃에 성공하였습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}