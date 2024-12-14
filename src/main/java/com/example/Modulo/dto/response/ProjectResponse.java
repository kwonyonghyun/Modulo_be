package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Project;
import lombok.Builder;
import lombok.Getter;

import java.time.YearMonth;
import java.util.List;

@Getter
@Builder
public class ProjectResponse {
    private Long id;
    private YearMonth startDate;
    private YearMonth endDate;
    private String projectName;
    private String shortDescription;
    private List<String> techStack;
    private String teamComposition;
    private String detailedDescription;

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectName(project.getProjectName())
                .shortDescription(project.getShortDescription())
                .techStack(project.getTechStack())
                .teamComposition(project.getTeamComposition())
                .detailedDescription(project.getDetailedDescription())
                .build();
    }
} 