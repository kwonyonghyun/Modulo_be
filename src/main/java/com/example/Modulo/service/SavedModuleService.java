package com.example.Modulo.service;

import com.example.Modulo.dto.response.SavedModuleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SavedModuleService {
    private final BasicInfoService basicInfoService;
    private final CareerService careerService;
    private final EducationService educationService;
    private final EtcService etcService;
    private final SelfIntroductionService selfIntroductionService;
    private final ProjectService projectService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_NAME = "savedModules";
    private static final long EXTEND_TTL_THRESHOLD = 30 * 60;
    private static final long EXTEND_TTL_DURATION = 60 * 60;

    @Cacheable(value = CACHE_NAME, key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public SavedModuleResponse getMySavedModules() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        String cacheKey = CACHE_NAME + "::" + memberId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        return SavedModuleResponse.builder()
                .basicInfos(new ArrayList<>(basicInfoService.getMyBasicInfos()))
                .careers(new ArrayList<>(careerService.getMyCareers()))
                .educations(new ArrayList<>(educationService.getMyEducations()))
                .etcs(new ArrayList<>(etcService.getMyEtcs()))
                .selfIntroductions(new ArrayList<>(selfIntroductionService.getMySelfIntroductions()))
                .projects(new ArrayList<>(projectService.getMyProjects()))
                .build();
    }
}