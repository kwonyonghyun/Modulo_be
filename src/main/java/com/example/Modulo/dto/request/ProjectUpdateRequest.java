package com.example.Modulo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProjectUpdateRequest {
    private YearMonth startDate;
    private YearMonth endDate;
    private String projectName;
    private String shortDescription;
    private List<String> techStack;
    private String teamComposition;
    private String detailedDescription;
} 