package com.example.Modulo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResumeCreateRequest {
    private String title;
    private List<ResumeSectionRequest> sections;
}