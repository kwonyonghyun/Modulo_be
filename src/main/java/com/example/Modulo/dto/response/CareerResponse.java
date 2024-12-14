package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Career;
import lombok.Builder;
import lombok.Getter;

import java.time.YearMonth;
import java.util.List;

@Getter
@Builder
public class CareerResponse {
    private Long id;
    private YearMonth startDate;
    private YearMonth endDate;
    private String companyName;
    private String companyDescription;
    private String position;
    private List<String> techStack;
    private String achievements;

    public static CareerResponse from(Career career) {
        return CareerResponse.builder()
                .id(career.getId())
                .startDate(career.getStartDate())
                .endDate(career.getEndDate())
                .companyName(career.getCompanyName())
                .companyDescription(career.getCompanyDescription())
                .position(career.getPosition())
                .techStack(career.getTechStack())
                .achievements(career.getAchievements())
                .build();
    }
}