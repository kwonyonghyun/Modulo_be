package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Education;
import com.example.Modulo.global.enums.EducationLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.YearMonth;

@Getter
@Builder
public class EducationResponse {
    private Long id;
    private YearMonth startDate;
    private YearMonth endDate;
    private String school;
    private String major;
    private EducationLevel educationLevel;

    public static EducationResponse from(Education education) {
        return EducationResponse.builder()
                .id(education.getId())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .school(education.getSchool())
                .major(education.getMajor())
                .educationLevel(education.getEducationLevel())
                .build();
    }
}