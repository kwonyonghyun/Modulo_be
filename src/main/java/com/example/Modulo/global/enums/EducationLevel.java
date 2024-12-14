package com.example.Modulo.global.enums;

import com.example.Modulo.exception.InvalidEducationLevelException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EducationLevel {
    HIGH_SCHOOL("고등학교"),
    COLLEGE_2_3("대학교(2,3년제)"),
    COLLEGE_4("대학교(4년제)"),
    MASTER("대학원(석사)"),
    DOCTOR("대학원(박사)");

    private final String description;

    public static EducationLevel fromDescription(String description) {
        for (EducationLevel level : values()) {
            if (level.getDescription().equals(description)) {
                return level;
            }
        }
        throw new InvalidEducationLevelException();
    }
}