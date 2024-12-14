package com.example.Modulo.service;

import com.example.Modulo.domain.Project;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.ProjectCreateRequest;
import com.example.Modulo.dto.request.ProjectUpdateRequest;
import com.example.Modulo.dto.response.ProjectResponse;
import com.example.Modulo.exception.ProjectNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.ProjectRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

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
                .techStack(request.getTechStack())
                .teamComposition(request.getTeamComposition())
                .detailedDescription(request.getDetailedDescription())
                .build();

        return projectRepository.save(project).getId();
    }

    public List<ProjectResponse> getMyProjects() {
        Long memberId = getCurrentMemberId();
        List<Project> projects = projectRepository.findAllByMemberId(memberId);
        return projects.stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateProject(Long projectId, ProjectUpdateRequest request) {
        Long memberId = getCurrentMemberId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        validateMemberAccess(project, memberId);

        project.update(
                request.getStartDate(),
                request.getEndDate(),
                request.getProjectName(),
                request.getShortDescription(),
                request.getTechStack(),
                request.getTeamComposition(),
                request.getDetailedDescription()
        );
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Long memberId = getCurrentMemberId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        validateMemberAccess(project, memberId);

        projectRepository.delete(project);
    }

    private void validateMemberAccess(Project project, Long memberId) {
        if (!project.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }
}
