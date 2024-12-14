package com.example.Modulo.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Global
    INTERNAL_SERVER_ERROR(500, "G001", "내부 서버 오류입니다."),
    METHOD_NOT_ALLOWED(405, "G002", "허용되지 않은 HTTP method입니다."),
    INPUT_VALUE_INVALID(400, "G003", "유효하지 않은 입력입니다."),
    INPUT_TYPE_INVALID(400, "G004", "입력 타입이 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(400, "G005", "request message body가 없거나, 값 타입이 올바르지 않습니다."),
    HTTP_HEADER_INVALID(400, "G006", "request header가 유효하지 않습니다."),
    SOCIAL_LOGIN_TOKEN_NOT_FOUND(500, "G019", "소셜 로그인 서버로부터 발급된 Access Token이 없습니다."),
    SOCIAL_LOGIN_USER_INFO_NOT_FOUND(500, "G020", "소셜 로그인 서버에서 조회한 유저 정보가 없습니다."),

    // Auth
    INVALID_TOKEN(400, "AU001", "유효하지 않은 토큰입니다."),
    ACCESS_DENIED(401, "AU005", "유효한 인증 정보가 아닙니다."),
    EXPIRED_ACCESS_TOKEN(401, "AU006", "Access Token이 만료되었습니다. 토큰을 재발급해주세요"),

    // Member
    MEMBER_NOT_FOUND(404, "M001", "존재하지 않는 유저입니다."),

    //Education
    INVALID_EDUCATION_LEVEL(400, "E001", "유효하지 않은 학력 레벨입니다."),
    EDUCATION_NOT_FOUND(404, "E002", "존재하지 않는 학력 정보입니다."),
    EDUCATION_CREATE_SUCCESS(200, "E003", "학력 정보가 성공적으로 생성되었습니다."),
    EDUCATION_UPDATE_SUCCESS(200, "E004", "학력 정보가 성공적으로 수정되었습니다."),
    EDUCATION_DELETE_SUCCESS(200, "E005", "학력 정보가 성공적으로 삭제되었습니다."),
    EDUCATION_GET_SUCCESS(200, "E006", "학력 정보 조회에 성공하였습니다."),
    UNAUTHORIZED_ACCESS(403, "E007", "해당 리소스에 대한 접근 권한이 없습니다."),

    //Career
    CAREER_NOT_FOUND(404, "CR001", "존재하지 않는 경력 정보입니다."),

    //Self Introduction
    SELF_INTRODUCTION_NOT_FOUND(404, "SI001", "존재하지 않는 자기소개서 정보입니다."),

    // Project
    PROJECT_NOT_FOUND(404, "PJ001", "프로젝트를 찾을 수 없습니다."),
    PROJECT_UNAUTHORIZED(403, "PJ002", "프로젝트에 대한 권한이 없습니다."),
    PROJECT_INVALID_DATE(400, "PJ003", "프로젝트 기간이 올바르지 않습니다."),

    // Etc
    ETC_NOT_FOUND(404, "ET001", "존재하지 않는 기타사항입니다."),
    UNAUTHORIZED_ETC_ACCESS(403, "ET002", "기타사항에 대한 권한이 없습니다."),

    // BasicInfo
    BASIC_INFO_NOT_FOUND(404, "BI001", "존재하지 않는 기본 정보입니다."),
    BASIC_INFO_UNAUTHORIZED(403, "BI002", "기본 정보에 대한 권한이 없습니다."),
    BASIC_INFO_DUPLICATE(400, "BI003", "이미 기본 정보가 존재합니다."),

    // File Upload
    FILE_UPLOAD_ERROR(500, "F001", "파일 업로드에 실패했습니다."),
    FILE_DELETE_ERROR(500, "F002", "파일 삭제에 실패했습니다."),
    INVALID_FILE_TYPE(400, "F003", "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEED(400, "F004", "파일 크기가 제한을 초과했습니다."),
    FILE_NOT_FOUND(404, "F005", "파일을 찾을 수 없습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}