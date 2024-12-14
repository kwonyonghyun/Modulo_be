package com.example.Modulo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Getter
@NoArgsConstructor
public class CareerCreateRequest {
    private YearMonth startDate;
    private YearMonth endDate;
    private String companyName;
    private String companyDescription;
    private String position;
    private List<String> techStack;
    private String achievements;
}