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

    //Education
    EDUCATION_CREATE_SUCCESS(200, "ED001", "학력 정보가 성공적으로 생성되었습니다."),
    EDUCATION_GET_SUCCESS(200, "ED002", "학력 정보 조회에 성공하였습니다."),
    EDUCATION_UPDATE_SUCCESS(200, "ED003", "학력 정보가 성공적으로 수정되었습니다."),
    EDUCATION_DELETE_SUCCESS(200, "ED004", "학력 정보가 성공적으로 삭제되었습니다."),

    //Career
    CAREER_CREATE_SUCCESS(200, "CR001", "경력 정보가 성공적으로 생성되었습니다."),
    CAREER_GET_SUCCESS(200, "CR002", "경력 정보 조회에 성공하���습니다."),
    CAREER_UPDATE_SUCCESS(200, "CR003", "경력 정보가 성공적으로 수정되었습니다."),
    CAREER_DELETE_SUCCESS(200, "CR004", "경력 정보가 성공적으로 삭제되었습니다."),


    //Self Introduction
    SELF_INTRODUCTION_CREATE_SUCCESS(200, "SL001", "자기소개서가 성공적으로 생성되었습니다. "),
    SELF_INTRODUCTION_GET_SUCCESS(200, "SL002","자기소개서 정보 조회에 성공하였습니다. "),
    SELF_INTRODUCTION_UPDATE_SUCCESS(200, "SL003","자기소개서 정보가 성공적으로 수정되었습니다. "),
    SELF_INTRODUCTION_DELETE_SUCCESS(200, "SL004", "자기소개서 정보가 성공적으로 삭제되었습니다. "),

    // Project
    PROJECT_CREATE_SUCCESS(200, "PJ001", "프로젝트가 성공적으로 생성되었습니다."),
    PROJECT_GET_SUCCESS(200, "PJ002", "프로젝트 정보 조회에 성공하였습니다."),
    PROJECT_UPDATE_SUCCESS(200, "PJ003", "프로젝트 정보가 성공적으로 수정되었습니다."),
    PROJECT_DELETE_SUCCESS(200, "PJ004", "프로젝트 정보가 성공적으로 삭제되었습니다."),

    // Etc
    ETC_CREATE_SUCCESS(200, "ET001", "기타사항이 성공적으로 생성되었습니다."),
    ETC_GET_SUCCESS(200, "ET002", "기타사항 조회에 성공하였습니다."),
    ETC_UPDATE_SUCCESS(200, "ET003", "기타사항이 성공적으로 수정되었습니다."),
    ETC_DELETE_SUCCESS(200, "ET004", "기타사항이 성공적으로 삭제되었습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}