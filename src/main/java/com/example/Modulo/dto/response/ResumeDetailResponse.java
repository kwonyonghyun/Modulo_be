package com.example.Modulo.dto.response;

import com.example.Modulo.domain.*;
import com.example.Modulo.service.*;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResumeDetailResponse {
    private Long id;
    private String title;
    private List<ResumeSectionDetailResponse> sections;

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