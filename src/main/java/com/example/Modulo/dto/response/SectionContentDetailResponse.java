package com.example.Modulo.dto.response;

import com.example.Modulo.domain.SectionContent;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.service.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionContentDetailResponse {
    private Long id;
    private Integer orderIndex;
    private Integer topMargin;
    private Object content;

    public static SectionContentDetailResponse from(SectionContent content,
                                                    SectionType sectionType,
                                                    BasicInfoService basicInfoService,
                                                    CareerService careerService,
                                                    EducationService educationService,
                                                    EtcService etcService,
                                                    ProjectService projectService,
                                                    SelfIntroductionService selfIntroductionService) {

        Object contentDetail = switch (sectionType) {
            case BASIC_INFO -> basicInfoService.getBasicInfoById(content.getContentId());
            case CAREER -> careerService.getCareerById(content.getContentId());
            case EDUCATION -> educationService.getEducationById(content.getContentId());
            case ETC -> etcService.getEtcById(content.getContentId());
            case PROJECT -> projectService.getProjectById(content.getContentId());
            case SELF_INTRODUCTION -> selfIntroductionService.getIntroductionById(content.getContentId());
        };

        return SectionContentDetailResponse.builder()
                .id(content.getId())
                .orderIndex(content.getOrderIndex())
                .topMargin(content.getTopMargin())
                .content(contentDetail)
                .build();
    }
}