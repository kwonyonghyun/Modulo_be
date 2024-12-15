package com.example.Modulo.dto.request;

import com.example.Modulo.global.enums.SectionType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResumeSectionRequest {
    private Integer orderIndex;
    private Integer topMargin;
    private SectionType sectionType;
    private List<SectionContentRequest> contents;
}