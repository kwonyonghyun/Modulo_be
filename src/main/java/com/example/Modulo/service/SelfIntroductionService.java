package com.example.Modulo.service;

import com.example.Modulo.domain.SelfIntroduction;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.SelfIntroductionCreateRequest;
import com.example.Modulo.dto.request.SelfIntroductionUpdateRequest;
import com.example.Modulo.dto.response.SelfIntroductionResponse;
import com.example.Modulo.exception.SelfIntroductionNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.SelfIntroductionRepository;
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
public class SelfIntroductionService {
    private static final String CACHE_NAME = "selfIntroduction";
    private static final long EXTEND_TTL_THRESHOLD = 30 * 60;
    private static final long EXTEND_TTL_DURATION = 60 * 60;

    private final SelfIntroductionRepository selfIntroductionRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createSelfIntroduction(SelfIntroductionCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        SelfIntroduction selfIntroduction = SelfIntroduction.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Long introductionId = selfIntroductionRepository.save(selfIntroduction).getId();

        String cacheKey = CACHE_NAME + "::selfIntroduction:" + introductionId;
        SelfIntroductionResponse response = SelfIntroductionResponse.from(selfIntroduction);
        redisTemplate.opsForValue().set(cacheKey, response, EXTEND_TTL_DURATION, TimeUnit.SECONDS);

        evictRelatedCaches();
        return introductionId;
    }

    @Cacheable(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public List<SelfIntroductionResponse> getMySelfIntroductions() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        String cacheKey = CACHE_NAME + "::member:" + memberId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        List<SelfIntroduction> selfIntroductions = selfIntroductionRepository.findAllByMemberId(Long.parseLong(memberId));
        return selfIntroductions.stream()
                .map(SelfIntroductionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void updateSelfIntroduction(Long selfIntroductionId, SelfIntroductionUpdateRequest request) {
        SelfIntroduction selfIntroduction = getSelfIntroduction(selfIntroductionId);
        validateMemberAccess(selfIntroduction, getCurrentMemberId());

        selfIntroduction.update(
                request.getTitle(),
                request.getContent()
        );

        evictRelatedCaches();
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteSelfIntroduction(Long selfIntroductionId) {
        SelfIntroduction selfIntroduction = getSelfIntroduction(selfIntroductionId);
        validateMemberAccess(selfIntroduction, getCurrentMemberId());

        selfIntroductionRepository.delete(selfIntroduction);
        evictRelatedCaches();
    }

    @Cacheable(value = CACHE_NAME, key = "'selfIntroduction:' + #selfIntroductionId")
    public SelfIntroductionResponse getIntroductionById(Long selfIntroductionId) {
        String cacheKey = CACHE_NAME + "::selfIntroduction:" + selfIntroductionId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        SelfIntroduction selfIntroduction = getSelfIntroduction(selfIntroductionId);
        validateMemberAccess(selfIntroduction, getCurrentMemberId());
        return SelfIntroductionResponse.from(selfIntroduction);
    }

    private SelfIntroduction getSelfIntroduction(Long selfIntroductionId) {
        return selfIntroductionRepository.findById(selfIntroductionId)
                .orElseThrow(SelfIntroductionNotFoundException::new);
    }

    private void validateMemberAccess(SelfIntroduction selfIntroduction, Long memberId) {
        if (!selfIntroduction.getMember().getId().equals(memberId)) {
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