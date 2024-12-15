package com.example.Modulo.dto.response;

import com.example.Modulo.domain.ResumeSection;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.service.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResumeSectionDetailResponse {
    private Long id;
    private Integer orderIndex;
    private Integer topMargin;
    private SectionType sectionType;
    private List<SectionContentDetailResponse> contents;

    public static ResumeSectionDetailResponse from(ResumeSection section,
                                                   BasicInfoService basicInfoService,
                                                   CareerService careerService,
                                                   EducationService educationService,
                                                   EtcService etcService,
                                                   ProjectService projectService,
                                                   SelfIntroductionService selfIntroductionService) {

        return ResumeSectionDetailResponse.builder()
                .id(section.getId())
                .orderIndex(section.getOrderIndex())
                .topMargin(section.getTopMargin())
                .sectionType(section.getSectionType())
                .contents(section.getContents().stream()
                        .map(content -> SectionContentDetailResponse.from(content,
                                section.getSectionType(),
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