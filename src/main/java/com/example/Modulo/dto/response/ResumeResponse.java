package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Resume;
import com.example.Modulo.global.enums.ResumeTheme;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponse implements Serializable {
    private Long id;
    private String title;
    private ResumeTheme theme;
    private List<ResumeSectionResponse> sections;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static ResumeResponse from(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .createdAt(resume.getCreatedAt())
                .title(resume.getTitle())
                .sections(resume.getSections().stream()
                        .map(ResumeSectionResponse::from)
                        .collect(Collectors.toList()))
                .theme(resume.getTheme())
                .build();
    }
}