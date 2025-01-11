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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {
    private static final String CACHE_NAME = "resumes";
    private static final long EXTEND_TTL_THRESHOLD = 30 * 60;
    private static final long EXTEND_TTL_DURATION = 60 * 60;

    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SavedModuleService savedModuleService;
    private final BasicInfoService basicInfoService;
    private final CareerService careerService;
    private final EducationService educationService;
    private final EtcService etcService;
    private final ProjectService projectService;
    private final SelfIntroductionService selfIntroductionService;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'resume-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public Long createResume(ResumeCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Resume resume = Resume.builder()
                .member(member)
                .title(request.getTitle())
                .build();

        validateAndCreateSections(request.getSections(), resume);
        Long resumeId = resumeRepository.save(resume).getId();

        return resumeId;
    }

    @Cacheable(value = CACHE_NAME, key = "'resume-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public List<ResumeResponse> getMyResumes() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        String cacheKey = CACHE_NAME + "::resume-member:" + memberId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        return resumeRepository.findAllByMemberId(Long.parseLong(memberId)).stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(ResumeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'resume-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void updateResume(Long resumeId, ResumeUpdateRequest request) {
        Resume resume = findResumeById(resumeId);
        validateMemberAccess(resume);

        resume.updateTitle(request.getTitle());
        validateAndCreateSections(request.getSections(), resume);
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'resume-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteResume(Long resumeId) {
        Resume resume = findResumeById(resumeId);
        validateMemberAccess(resume);

        resumeRepository.delete(resume);
    }

    @Cacheable(value = CACHE_NAME, key = "'resume-content:' + #resumeId")
    public ResumeResponse getResumeById(Long resumeId) {
        String cacheKey = CACHE_NAME + "::resume-content:" + resumeId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        Resume resume = findResumeById(resumeId);
        validateMemberAccess(resume);
        return ResumeResponse.from(resume);
    }

    @Cacheable(value = CACHE_NAME, key = "'resume-detail:' + #resumeId")
    public ResumeDetailResponse getResumeDetailById(Long resumeId) {
        String cacheKey = CACHE_NAME + "::resume-detail:" + resumeId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        Resume resume = findResumeById(resumeId);
        validateMemberAccess(resume);

        return ResumeDetailResponse.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .createdAt(resume.getCreatedAt())
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

    private Resume findResumeById(Long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(ResumeNotFoundException::new);
    }

    private void validateMemberAccess(Resume resume) {
        if (!resume.getMember().getId().equals(getCurrentMemberId())) {
            throw new UnauthorizedAccessException();
        }
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
                throw new InvalidSectionContentException();
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
}