package com.example.Modulo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SectionContentRequest {
    private Integer orderIndex;
    private Integer topMargin;
    private Long contentId;
}