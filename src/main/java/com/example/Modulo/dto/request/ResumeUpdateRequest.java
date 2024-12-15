package com.example.Modulo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResumeUpdateRequest {
    private String title;
    private List<ResumeSectionRequest> sections;
}