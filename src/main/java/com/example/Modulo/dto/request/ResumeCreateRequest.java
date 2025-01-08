package com.example.Modulo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class ResumeCreateRequest {
    private String title;
    private List<ResumeSectionRequest> sections;
}