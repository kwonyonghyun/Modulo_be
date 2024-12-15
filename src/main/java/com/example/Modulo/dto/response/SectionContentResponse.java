package com.example.Modulo.dto.response;

import com.example.Modulo.domain.SectionContent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionContentResponse {
    private Long id;
    private Integer orderIndex;
    private Integer topMargin;
    private Long contentId;

    public static SectionContentResponse from(SectionContent content) {
        return SectionContentResponse.builder()
                .id(content.getId())
                .orderIndex(content.getOrderIndex())
                .topMargin(content.getTopMargin())
                .contentId(content.getContentId())
                .build();
    }
}