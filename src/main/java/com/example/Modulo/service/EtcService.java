package com.example.Modulo.service;

import com.example.Modulo.domain.Etc;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.EtcCreateRequest;
import com.example.Modulo.dto.request.EtcUpdateRequest;
import com.example.Modulo.dto.response.EtcResponse;
import com.example.Modulo.exception.EtcNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.EtcRepository;
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
public class EtcService {
    private static final String CACHE_NAME = "etc";
    private static final long EXTEND_TTL_THRESHOLD = 30 * 60;
    private static final long EXTEND_TTL_DURATION = 60 * 60;

    private final EtcRepository etcRepository;
    private final MemberRepository memberRepository;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ResumeSectionHandler resumeSectionHandler;

    @Transactional
    public Long createEtc(EtcCreateRequest request) {
        Member member = getMemberFromToken();

        Etc etc = Etc.builder()
                .member(member)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .organization(request.getOrganization())
                .score(request.getScore())
                .build();

        Long etcId = etcRepository.save(etc).getId();

        String cacheKey = CACHE_NAME + "::etc:" + etcId;
        EtcResponse response = EtcResponse.from(etc);
        redisTemplate.opsForValue().set(cacheKey, response, EXTEND_TTL_DURATION, TimeUnit.SECONDS);

        evictRelatedCaches();
        return etcId;
    }

    @Cacheable(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public List<EtcResponse> getMyEtcs() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        String cacheKey = CACHE_NAME + "::member:" + memberId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        return etcRepository.findAllByMemberId(Long.parseLong(memberId)).stream()
                .map(EtcResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void updateEtc(Long id, EtcUpdateRequest request) {
        Etc etc = etcRepository.findById(id)
                .orElseThrow(EtcNotFoundException::new);

        validateMemberAccess(etc);

        etc.update(
                request.getStartDate(),
                request.getEndDate(),
                request.getTitle(),
                request.getDescription(),
                request.getType(),
                request.getOrganization(),
                request.getScore()
        );

        evictRelatedCaches();
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteEtc(Long id) {
        Etc etc = etcRepository.findById(id)
                .orElseThrow(EtcNotFoundException::new);

        validateMemberAccess(etc);

        etcRepository.delete(etc);
        evictRelatedCaches();
        resumeSectionHandler.handleContentDeletion(id, SectionType.ETC);
    }

    @Cacheable(value = CACHE_NAME, key = "'etc:' + #id")
    public EtcResponse getEtcById(Long id) {
        String cacheKey = CACHE_NAME + "::etc:" + id;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        Etc etc = etcRepository.findById(id)
                .orElseThrow(EtcNotFoundException::new);

        validateMemberAccess(etc);
        return EtcResponse.from(etc);
    }

    private Member getMemberFromToken() {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateMemberAccess(Etc etc) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!etc.getMember().getId().equals(memberId)) {
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
