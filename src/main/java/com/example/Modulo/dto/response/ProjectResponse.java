package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Project;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse implements Serializable {
    private Long id;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth startDate;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
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
                .techStack(new ArrayList<>(project.getTechStack()))
                .teamComposition(project.getTeamComposition())
                .detailedDescription(project.getDetailedDescription())
                .build();
    }
} 