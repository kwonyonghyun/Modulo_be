package com.example.Modulo.service;

import com.example.Modulo.domain.Project;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.ProjectCreateRequest;
import com.example.Modulo.dto.request.ProjectUpdateRequest;
import com.example.Modulo.dto.response.ProjectResponse;
import com.example.Modulo.exception.ProjectNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.ProjectRepository;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private static final String CACHE_NAME = "project";
    private static final long EXTEND_TTL_THRESHOLD = 30 * 60;
    private static final long EXTEND_TTL_DURATION = 60 * 60;

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ResumeSectionHandler resumeSectionHandler;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createProject(ProjectCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Project project = Project.builder()
                .member(member)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .projectName(request.getProjectName())
                .shortDescription(request.getShortDescription())
                .techStack(new ArrayList<>(request.getTechStack()))
                .teamComposition(request.getTeamComposition())
                .detailedDescription(request.getDetailedDescription())
                .build();

        Long projectId = projectRepository.save(project).getId();

        ProjectResponse response = ProjectResponse.from(project);
        String cacheKey = CACHE_NAME + "::project-content:" + projectId;
        redisTemplate.opsForValue().set(cacheKey, response, EXTEND_TTL_DURATION, TimeUnit.SECONDS);

        evictRelatedCaches();
        return projectId;
    }

    @Cacheable(value = CACHE_NAME, key = "'project-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public List<ProjectResponse> getMyProjects() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        String cacheKey = CACHE_NAME + "::project-member:" + memberId;

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        List<Project> projects = projectRepository.findAllByMemberId(Long.parseLong(memberId));
        return projects.stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'project-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void updateProject(Long projectId, ProjectUpdateRequest request) {
        Project project = findProjectById(projectId);
        validateMemberAccess(project);

        project.update(
                request.getStartDate(),
                request.getEndDate(),
                request.getProjectName(),
                request.getShortDescription(),
                request.getTechStack(),
                request.getTeamComposition(),
                request.getDetailedDescription()
        );

        evictRelatedCaches();
    }

    @Transactional
    @CacheEvict(value = CACHE_NAME, key = "'project-member:' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteProject(Long projectId) {
        Project project = findProjectById(projectId);
        validateMemberAccess(project);

        projectRepository.delete(project);
        evictRelatedCaches();
        resumeSectionHandler.handleContentDeletion(projectId, SectionType.PROJECT);
    }

    @Cacheable(value = CACHE_NAME, key = "'project-content:' + #projectId")
    public ProjectResponse getProjectById(Long projectId) {
        String cacheKey = CACHE_NAME + "::project-content:" + projectId;

        Project project = findProjectById(projectId);
        validateMemberAccess(project);
        ProjectResponse response = ProjectResponse.from(project);

        Long ttl = redisTemplate.getExpire(cacheKey);
        if (ttl != null && ttl < EXTEND_TTL_THRESHOLD) {
            redisTemplate.expire(cacheKey, EXTEND_TTL_DURATION, TimeUnit.SECONDS);
        }

        return response;
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);
    }

    private void validateMemberAccess(Project project) {
        Long currentMemberId = getCurrentMemberId();
        if (!project.getMember().getId().equals(currentMemberId)) {
            throw new UnauthorizedAccessException();
        }
    }

    private void evictRelatedCaches() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        redisTemplate.delete(CACHE_NAME + "::project-member:" + memberId);
        redisTemplate.delete("savedModules::savedmodule-member:" + memberId);
        redisTemplate.delete("resumes::resume-member:" + memberId);
    }
}