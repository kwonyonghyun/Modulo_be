package com.example.Modulo.service;

import com.example.Modulo.domain.BasicInfo;
import com.example.Modulo.domain.Link;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.BasicInfoCreateRequest;
import com.example.Modulo.dto.request.BasicInfoUpdateRequest;
import com.example.Modulo.dto.response.BasicInfoResponse;
import com.example.Modulo.exception.BasicInfoNotFoundException;
import com.example.Modulo.exception.BasicInfoUnauthorizedException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.global.service.S3Service;
import com.example.Modulo.repository.BasicInfoRepository;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicInfoService {
    private static final String CACHE_NAME = "basicInfo";
    private static final String PROFILE_IMAGE_DIRECTORY = "profile-images";
    private static final long EXTEND_TTL_THRESHOLD = 30 * 60;
    private static final long EXTEND_TTL_DURATION = 60 * 60;

    private final BasicInfoRepository basicInfoRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ResumeSectionHandler resumeSectionHandler;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createBasicInfo(BasicInfoCreateRequest request, MultipartFile profileImage) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(PROFILE_IMAGE_DIRECTORY, profileImage);
        }

        BasicInfo basicInfo = BasicInfo.builder()
                .member(member)
                .profileImageUrl(profileImageUrl)
                .name(request.getName())
                .email(request.getEmail())
                .careerYear(request.getCareerYear())
                .birthYear(request.getBirthYear())
                .jobPosition(request.getJobPosition())
                .shortBio(request.getShortBio())
                .build();

        List<Link> links = request.getLinks() != null ?
                request.getLinks().stream()
                        .map(linkRequest -> Link.builder()
                                .title(linkRequest.getTitle())
                                .url(linkRequest.getUrl())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        basicInfo.updateLinks(links);
        basicInfo.updateTechStack(request.getTechStack());

        Long basicInfoId = basicInfoRepository.save(basicInfo).getId();

        String cacheKey = CACHE_NAME + "::basicinfo-content:" + basicInfoId;
        BasicInfoResponse response = BasicInfoResponse.from(basicInfo);
        redisTemplate.opsForValue().set(cacheKey, response, EXTEND_TTL_DURATION, TimeUnit.SECONDS);

        evictRelatedCaches();
        return basicInfoId;
    }

    @Cacheable(value = CACHE_NAME, key = "'basicinfo-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public List<BasicInfoResponse> getMyBasicInfos() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        String cacheKey = CACHE_NAME + "::basicinfo-member:" + memberId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        return basicInfoRepository.findAllByMemberId(Long.parseLong(memberId)).stream()
                .map(BasicInfoResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'basicinfo-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void updateBasicInfo(Long id, BasicInfoUpdateRequest request, MultipartFile profileImage) {
        BasicInfo basicInfo = findBasicInfoById(id);
        validateMemberAccess(basicInfo);

        String profileImageUrl = basicInfo.getProfileImageUrl();
        if (profileImage != null && !profileImage.isEmpty()) {
            if (profileImageUrl != null) {
                s3Service.deleteFile(profileImageUrl);
            }
            profileImageUrl = s3Service.uploadFile(PROFILE_IMAGE_DIRECTORY, profileImage);
        }

        List<Link> links = request.getLinks() != null ?
                request.getLinks().stream()
                        .map(linkRequest -> Link.builder()
                                .title(linkRequest.getTitle())
                                .url(linkRequest.getUrl())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        basicInfo.update(
                profileImageUrl,
                request.getName(),
                request.getEmail(),
                request.getCareerYear(),
                request.getBirthYear(),
                request.getJobPosition(),
                request.getShortBio()
        );
        basicInfo.updateLinks(links);
        basicInfo.updateTechStack(request.getTechStack());

        evictContentCache(id);
        evictRelatedCaches();
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'basicinfo-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteBasicInfo(Long id) {
        BasicInfo basicInfo = findBasicInfoById(id);
        validateMemberAccess(basicInfo);

        if (basicInfo.getProfileImageUrl() != null) {
            s3Service.deleteFile(basicInfo.getProfileImageUrl());
        }

        basicInfoRepository.delete(basicInfo);
        evictContentCache(id);
        evictRelatedCaches();
        resumeSectionHandler.handleContentDeletion(id, SectionType.BASIC_INFO);
    }

    @Cacheable(value = CACHE_NAME, key = "'basicinfo-content:' + #id")
    public BasicInfoResponse getBasicInfoById(Long id) {
        String cacheKey = CACHE_NAME + "::basicinfo-content:" + id;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        BasicInfo basicInfo = findBasicInfoById(id);
        validateMemberAccess(basicInfo);
        return BasicInfoResponse.from(basicInfo);
    }

    private BasicInfo findBasicInfoById(Long id) {
        return basicInfoRepository.findById(id)
                .orElseThrow(BasicInfoNotFoundException::new);
    }

    private void validateMemberAccess(BasicInfo basicInfo) {
        if (!basicInfo.getMember().getId().equals(getCurrentMemberId())) {
            throw new BasicInfoUnauthorizedException();
        }
    }

    private void evictRelatedCaches() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        redisTemplate.delete(CACHE_NAME + "::basicinfo-member:" + memberId);
        redisTemplate.delete("savedModules::savedmodule-member:" + memberId);
        redisTemplate.delete("resumes::resume-member:" + memberId);
    }

    private void evictContentCache(Long contentId) {
        redisTemplate.delete(CACHE_NAME + "::basicinfo-content:" + contentId);
    }

}