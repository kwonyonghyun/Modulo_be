package com.example.Modulo.dto.response;

import com.example.Modulo.domain.*;
import com.example.Modulo.global.enums.ResumeTheme;
import com.example.Modulo.service.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDetailResponse implements Serializable {
    private Long id;
    private String title;
    private ResumeTheme theme;
    private List<ResumeSectionDetailResponse> sections;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static ResumeDetailResponse from(Resume resume,
                                            BasicInfoService basicInfoService,
                                            CareerService careerService,
                                            EducationService educationService,
                                            EtcService etcService,
                                            ProjectService projectService,
                                            SelfIntroductionService selfIntroductionService) {

        return ResumeDetailResponse.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .theme(resume.getTheme())
                .createdAt(resume.getCreatedAt())
                .sections(resume.getSections().stream()
                        .map(section -> ResumeSectionDetailResponse.from(section,
                                basicInfoService,
                                careerService,
                                educationService,
                                etcService,
                                projectService,
                                selfIntroductionService))
                        .collect(Collectors.toList()))
                .build();
    }
}