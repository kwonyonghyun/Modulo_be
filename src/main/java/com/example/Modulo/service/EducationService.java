package com.example.Modulo.service;

import com.example.Modulo.domain.Education;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.EducationCreateRequest;
import com.example.Modulo.dto.request.EducationUpdateRequest;
import com.example.Modulo.dto.response.EducationResponse;
import com.example.Modulo.exception.EducationNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.EducationRepository;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
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
public class EducationService {
    private static final String CACHE_NAME = "education";
    private static final long EXTEND_TTL_THRESHOLD = 30 * 60;
    private static final long EXTEND_TTL_DURATION = 60 * 60;

    private final EducationRepository educationRepository;
    private final MemberRepository memberRepository;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ResumeSectionHandler resumeSectionHandler;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createEducation(EducationCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Education education = Education.builder()
                .member(member)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .school(request.getSchool())
                .major(request.getMajor())
                .educationLevel(request.getEducationLevel())
                .build();

        Long educationId = educationRepository.save(education).getId();

        String cacheKey = CACHE_NAME + "::education:" + educationId;
        EducationResponse response = EducationResponse.from(education);
        redisTemplate.opsForValue().set(cacheKey, response, EXTEND_TTL_DURATION, TimeUnit.SECONDS);

        evictRelatedCaches();
        return educationId;
    }

    @Cacheable(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public List<EducationResponse> getMyEducations() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        String cacheKey = CACHE_NAME + "::member:" + memberId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        List<Education> educations = educationRepository.findAllByMemberId(Long.parseLong(memberId));
        return educations.stream()
                .map(EducationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void updateEducation(Long educationId, EducationUpdateRequest request) {
        Education education = getEducation(educationId);
        validateMemberAccess(education);

        education.update(
                request.getStartDate(),
                request.getEndDate(),
                request.getSchool(),
                request.getMajor(),
                request.getEducationLevel()
        );

        evictRelatedCaches();
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteEducation(Long educationId) {
        Education education = getEducation(educationId);
        validateMemberAccess(education);

        educationRepository.delete(education);
        evictRelatedCaches();
        resumeSectionHandler.handleContentDeletion(educationId, SectionType.EDUCATION);
    }

    @Cacheable(value = CACHE_NAME, key = "'education:' + #educationId")
    public EducationResponse getEducationById(Long educationId) {
        String cacheKey = CACHE_NAME + "::education:" + educationId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        Education education = getEducation(educationId);
        validateMemberAccess(education);
        return EducationResponse.from(education);
    }

    private Education getEducation(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(EducationNotFoundException::new);
    }

    private void validateMemberAccess(Education education) {
        if (!education.getMember().getId().equals(getCurrentMemberId())) {
            throw new UnauthorizedAccessException();
        }
    }

    private void validateMemberAccess(Education education, Long memberId) {
        if (!education.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }

    private void evictRelatedCaches() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        redisTemplate.delete(CACHE_NAME + "::member:" + memberId);
        redisTemplate.delete("savedModules::member:" + memberId);
        redisTemplate.delete("resumes::member:" + memberId);
    }
}