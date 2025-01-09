package com.example.Modulo.service;

import com.example.Modulo.domain.Resume;
import com.example.Modulo.domain.ResumeSection;
import com.example.Modulo.domain.SectionContent;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResumeSectionHandler {
    private final ResumeRepository resumeRepository;

    public void handleContentDeletion(Long contentId, SectionType sectionType) {
        List<Resume> resumes = resumeRepository.findAll();

        for (Resume resume : resumes) {
            List<ResumeSection> sections = resume.getSections();

            for (ResumeSection section : sections) {
                if (section.getSectionType() == sectionType) {
                    List<SectionContent> contents = section.getContents();
                    contents.removeIf(content -> content.getContentId().equals(contentId));
                }
            }
        }

        resumeRepository.saveAll(resumes);
    }
}