package com.example.Modulo.dto.response;

import com.example.Modulo.domain.SectionContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionContentResponse implements Serializable {
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