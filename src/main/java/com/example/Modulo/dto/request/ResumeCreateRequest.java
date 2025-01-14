package com.example.Modulo.dto.request;

import com.example.Modulo.global.enums.ResumeTheme;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class ResumeCreateRequest {
    private String title;
    private ResumeTheme theme;
    private List<ResumeSectionRequest> sections;
}