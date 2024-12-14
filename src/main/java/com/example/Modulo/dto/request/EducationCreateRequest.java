package com.example.Modulo.dto.request;

import com.example.Modulo.global.enums.EducationLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Getter
@NoArgsConstructor
public class EducationCreateRequest {
    private YearMonth startDate;
    private YearMonth endDate;
    private String school;
    private String major;
    private EducationLevel educationLevel;
}