package com.example.Modulo.dto.response;

import com.example.Modulo.domain.ResumeSection;
import com.example.Modulo.global.enums.SectionType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResumeSectionResponse {
    private Long id;
    private Integer orderIndex;
    private Integer topMargin;
    private SectionType sectionType;
    private List<SectionContentResponse> contents;

    public static ResumeSectionResponse from(ResumeSection section) {
        return ResumeSectionResponse.builder()
                .id(section.getId())
                .orderIndex(section.getOrderIndex())
                .topMargin(section.getTopMargin())
                .sectionType(section.getSectionType())
                .contents(section.getContents().stream()
                        .map(SectionContentResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}