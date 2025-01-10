package com.example.Modulo.service;

import com.example.Modulo.domain.Career;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.CareerCreateRequest;
import com.example.Modulo.dto.request.CareerUpdateRequest;
import com.example.Modulo.dto.response.CareerResponse;
import com.example.Modulo.exception.CareerNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.CareerRepository;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
public class CareerService {
    private static final String CACHE_NAME = "career";
    private static final long EXTEND_TTL_THRESHOLD = 30 * 60;
    private static final long EXTEND_TTL_DURATION = 60 * 60;

    private final CareerRepository careerRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ResumeSectionHandler resumeSectionHandler;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createCareer(CareerCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Career career = Career.builder()
                .member(member)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .companyName(request.getCompanyName())
                .companyDescription(request.getCompanyDescription())
                .position(request.getPosition())
                .techStack(request.getTechStack())
                .achievements(request.getAchievements())
                .build();

        Long careerId = careerRepository.save(career).getId();

        String cacheKey = CACHE_NAME + "::career:" + careerId;
        CareerResponse response = CareerResponse.from(career);
        redisTemplate.opsForValue().set(cacheKey, response, EXTEND_TTL_DURATION, TimeUnit.SECONDS);

        evictRelatedCaches();
        return careerId;
    }

    @Cacheable(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public List<CareerResponse> getMyCareers() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        String cacheKey = CACHE_NAME + "::member:" + memberId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        List<Career> careers = careerRepository.findAllByMemberId(Long.parseLong(memberId));
        return careers.stream()
                .map(CareerResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void updateCareer(Long careerId, CareerUpdateRequest request) {
        Career career = getCareer(careerId);
        validateMemberAccess(career);

        career.update(
                request.getStartDate(),
                request.getEndDate(),
                request.getCompanyName(),
                request.getCompanyDescription(),
                request.getPosition(),
                request.getTechStack(),
                request.getAchievements()
        );

        evictRelatedCaches();
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteCareer(Long careerId) {
        Career career = getCareer(careerId);
        validateMemberAccess(career);

        careerRepository.delete(career);
        evictRelatedCaches();
        resumeSectionHandler.handleContentDeletion(careerId, SectionType.CAREER);
    }

    @Cacheable(value = CACHE_NAME, key = "'career:' + #careerId")
    public CareerResponse getCareerById(Long careerId) {
        String cacheKey = CACHE_NAME + "::career:" + careerId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        Career career = getCareer(careerId);
        validateMemberAccess(career);
        return CareerResponse.from(career);
    }

    private Career getCareer(Long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(CareerNotFoundException::new);
    }

    private void validateMemberAccess(Career career) {
        Long memberId = getCurrentMemberId();
        if (!career.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }

    private void validateMemberAccess(Career career, Long memberId) {
        if (!career.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }

    private void evictRelatedCaches() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        redisTemplate.delete(CACHE_NAME + "::member:" + memberId);
        redisTemplate.delete("savedModules::" + memberId);
        redisTemplate.delete("resumes::" + memberId);
    }
}