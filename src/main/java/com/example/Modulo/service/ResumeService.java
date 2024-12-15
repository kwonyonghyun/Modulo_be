package com.example.Modulo.service;

import com.example.Modulo.domain.*;
import com.example.Modulo.dto.request.ResumeCreateRequest;
import com.example.Modulo.dto.request.ResumeUpdateRequest;
import com.example.Modulo.dto.request.ResumeSectionRequest;
import com.example.Modulo.dto.request.SectionContentRequest;
import com.example.Modulo.dto.response.ResumeDetailResponse;
import com.example.Modulo.dto.response.ResumeResponse;
import com.example.Modulo.dto.response.ResumeSectionDetailResponse;
import com.example.Modulo.dto.response.SectionContentDetailResponse;
import com.example.Modulo.exception.InvalidSectionContentException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.ResumeNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.ResumeRepository;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;
    private final BasicInfoService basicInfoService;
    private final CareerService careerService;
    private final EducationService educationService;
    private final EtcService etcService;
    private final ProjectService projectService;
    private final SavedModuleService savedModuleService;
    private final SelfIntroductionService selfIntroductionService;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createResume(ResumeCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Resume resume = Resume.builder()
                .member(member)
                .title(request.getTitle())
                .build();

        validateAndCreateSections(request.getSections(), resume);

        return resumeRepository.save(resume).getId();
    }

    @Transactional
    public void updateResume(Long resumeId, ResumeUpdateRequest request) {
        Long memberId = getCurrentMemberId();
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(ResumeNotFoundException::new);

        validateMemberAccess(resume, memberId);

        resume.updateTitle(request.getTitle());
        validateAndCreateSections(request.getSections(), resume);
    }

    private void validateAndCreateSections(List<ResumeSectionRequest> sectionRequests, Resume resume) {
        List<ResumeSection> sections = createResumeSections(sectionRequests, resume);
        resume.updateSections(sections);
    }

    private List<ResumeSection> createResumeSections(List<ResumeSectionRequest> sectionRequests, Resume resume) {
        return sectionRequests.stream()
                .map(request -> {
                    validateSectionContent(request.getSectionType(), request.getContents());

                    ResumeSection section = ResumeSection.builder()
                            .resume(resume)
                            .orderIndex(request.getOrderIndex())
                            .topMargin(request.getTopMargin())
                            .sectionType(request.getSectionType())
                            .build();

                    List<SectionContent> contents = createSectionContents(request.getContents(), section);
                    section.updateContents(contents);

                    return section;
                })
                .collect(Collectors.toList());
    }

    private void validateSectionContent(SectionType sectionType, List<SectionContentRequest> contents) {
        contents.forEach(content -> {
            try {
                switch (sectionType) {
                    case BASIC_INFO -> basicInfoService.getBasicInfoById(content.getContentId());
                    case CAREER -> careerService.getCareerById(content.getContentId());
                    case EDUCATION -> educationService.getEducationById(content.getContentId());
                    case ETC -> etcService.getEtcById(content.getContentId());
                    case PROJECT -> projectService.getProjectById(content.getContentId());
                    case SELF_INTRODUCTION -> selfIntroductionService.getIntroductionById(content.getContentId());
                }
            } catch (Exception e) {
                throw new InvalidSectionContentException(sectionType, content.getContentId());
            }
        });
    }



    private List<SectionContent> createSectionContents(List<SectionContentRequest> contentRequests, ResumeSection section) {
        return contentRequests.stream()
                .map(request -> SectionContent.builder()
                        .resumeSection(section)
                        .orderIndex(request.getOrderIndex())
                        .topMargin(request.getTopMargin())
                        .contentId(request.getContentId())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ResumeResponse> getMyResumes() {
        Long memberId = getCurrentMemberId();
        List<Resume> resumes = resumeRepository.findAllByMemberId(memberId);
        return resumes.stream()
                .map(ResumeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteResume(Long resumeId) {
        Long memberId = getCurrentMemberId();
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(ResumeNotFoundException::new);

        validateMemberAccess(resume, memberId);

        resumeRepository.delete(resume);
    }

    public ResumeResponse getResumeById(Long resumeId) {
        Long memberId = getCurrentMemberId();
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(ResumeNotFoundException::new);

        validateMemberAccess(resume, memberId);

        return ResumeResponse.from(resume);
    }

    private void validateMemberAccess(Resume resume, Long memberId) {
        if (!resume.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }

    @Transactional(readOnly = true)
    public ResumeDetailResponse getResumeDetailById(Long resumeId) {
        Long memberId = getCurrentMemberId();
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(ResumeNotFoundException::new);

        validateMemberAccess(resume, memberId);

        return ResumeDetailResponse.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .sections(resume.getSections().stream()
                        .map(section -> {
                            List<Object> contentDetails = section.getContents().stream()
                                    .map(content -> switch (section.getSectionType()) {
                                        case BASIC_INFO -> basicInfoService.getBasicInfoById(content.getContentId());
                                        case CAREER -> careerService.getCareerById(content.getContentId());
                                        case EDUCATION -> educationService.getEducationById(content.getContentId());
                                        case ETC -> etcService.getEtcById(content.getContentId());
                                        case PROJECT -> projectService.getProjectById(content.getContentId());
                                        case SELF_INTRODUCTION -> selfIntroductionService.getIntroductionById(content.getContentId());
                                    })
                                    .collect(Collectors.toList());

                            return ResumeSectionDetailResponse.builder()
                                    .id(section.getId())
                                    .orderIndex(section.getOrderIndex())
                                    .topMargin(section.getTopMargin())
                                    .sectionType(section.getSectionType())
                                    .contents(section.getContents().stream()
                                            .map(content -> SectionContentDetailResponse.builder()
                                                    .id(content.getId())
                                                    .orderIndex(content.getOrderIndex())
                                                    .topMargin(content.getTopMargin())
                                                    .content(contentDetails.get(content.getOrderIndex() - 1))
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
    }
}