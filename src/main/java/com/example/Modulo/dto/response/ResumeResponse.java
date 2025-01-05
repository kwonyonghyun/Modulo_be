package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Resume;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResumeResponse {
    private Long id;
    private String title;
    private List<ResumeSectionResponse> sections;
    private LocalDateTime createdAt;

    public static ResumeResponse from(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .createdAt(resume.getCreatedAt())
                .title(resume.getTitle())
                .sections(resume.getSections().stream()
                        .map(ResumeSectionResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}