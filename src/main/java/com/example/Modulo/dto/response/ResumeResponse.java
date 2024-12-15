package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Resume;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResumeResponse {
    private Long id;
    private String title;
    private List<ResumeSectionResponse> sections;

    public static ResumeResponse from(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .sections(resume.getSections().stream()
                        .map(ResumeSectionResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}