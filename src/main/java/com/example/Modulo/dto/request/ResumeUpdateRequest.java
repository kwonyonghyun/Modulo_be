package com.example.Modulo.dto.request;

import com.example.Modulo.global.enums.ResumeTheme;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResumeUpdateRequest {
    private String title;
    private ResumeTheme theme;
    private List<ResumeSectionRequest> sections;
}