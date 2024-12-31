package com.example.Modulo.service;

import com.example.Modulo.domain.Member;
import com.example.Modulo.domain.Project;
import com.example.Modulo.dto.request.ProjectCreateRequest;
import com.example.Modulo.dto.request.ProjectUpdateRequest;
import com.example.Modulo.dto.response.ProjectResponse;
import com.example.Modulo.exception.ProjectNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.MemberRepository;
import com.example.Modulo.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Member member;
    private Project project;
    private ProjectCreateRequest createRequest;
    private ProjectUpdateRequest updateRequest;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();

        Field memberIdField = member.getClass().getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(member, 1L);

        project = Project.builder()
                .member(member)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .projectName("테스트 프로젝트")
                .shortDescription("프로젝트 간단 설명")
                .techStack(Arrays.asList("Java", "Spring"))
                .teamComposition("백엔드 2명, 프론트엔드 2명")
                .detailedDescription("상세 설명")
                .build();

        Field projectIdField = project.getClass().getDeclaredField("id");
        projectIdField.setAccessible(true);
        projectIdField.set(project, 1L);

        createRequest = new ProjectCreateRequest();
        setFieldValue(createRequest, "startDate", YearMonth.of(2023, 1));
        setFieldValue(createRequest, "endDate", YearMonth.of(2023, 12));
        setFieldValue(createRequest, "projectName", "테스트 프로젝트");
        setFieldValue(createRequest, "shortDescription", "프로젝트 간단 설명");
        setFieldValue(createRequest, "techStack", Arrays.asList("Java", "Spring"));
        setFieldValue(createRequest, "teamComposition", "백엔드 2명, 프론트엔드 2명");
        setFieldValue(createRequest, "detailedDescription", "상세 설명");

        updateRequest = new ProjectUpdateRequest();
        setFieldValue(updateRequest, "startDate", YearMonth.of(2023, 1));
        setFieldValue(updateRequest, "endDate", YearMonth.of(2023, 12));
        setFieldValue(updateRequest, "projectName", "수정된 프로젝트");
        setFieldValue(updateRequest, "shortDescription", "수정된 설명");
        setFieldValue(updateRequest, "techStack", Arrays.asList("Java", "Spring", "React"));
        setFieldValue(updateRequest, "teamComposition", "백엔드 3명, 프론트엔드 3명");
        setFieldValue(updateRequest, "detailedDescription", "수정된 상세 설명");
    }

    private void setupAuthentication() {
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("1");
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @DisplayName("프로젝트 생성 성공")
    void createProject_Success() {
        // given
        setupAuthentication();
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(projectRepository.save(any(Project.class))).willReturn(project);

        // when
        Long projectId = projectService.createProject(createRequest);

        // then
        assertThat(projectId).isEqualTo(1L);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 단건 조회 성공")
    void getMyProject_Success() {
        //given
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));
        setupAuthentication();

        //when
        ProjectResponse result = projectService.getProjectById(1L);

        //then
        assertThat(result.getProjectName()).isEqualTo(project.getProjectName());
        assertThat(result.getId()).isEqualTo(project.getId());
    }

    @Test
    @DisplayName("내 프로젝트 목록 조회 성공")
    void getMyProjects_Success() {
        // given
        setupAuthentication();
        given(projectRepository.findAllByMemberId(1L)).willReturn(List.of(project));

        // when
        List<ProjectResponse> result = projectService.getMyProjects();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(project.getId());
        assertThat(result.get(0).getProjectName()).isEqualTo(project.getProjectName());
    }

    @Test
    @DisplayName("프로젝트 수정 성공")
    void updateProject_Success() {
        // given
        setupAuthentication();
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));

        // when
        projectService.updateProject(1L, updateRequest);

        // then
        assertThat(project.getProjectName()).isEqualTo(updateRequest.getProjectName());
        assertThat(project.getShortDescription()).isEqualTo(updateRequest.getShortDescription());
    }

    @Test
    @DisplayName("프로젝트 삭제 성공")
    void deleteProject_Success() {
        // given
        setupAuthentication();
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));

        // when
        projectService.deleteProject(1L);

        // then
        verify(projectRepository).delete(project);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 수정 시 예외 발생")
    void updateProject_NotFound() {
        // given
        setupAuthentication();
        given(projectRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectService.updateProject(1L, updateRequest))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("권한 없는 프로젝트 수정 시 예외 발생")
    void updateProject_Unauthorized() throws Exception {
        // given
        setupAuthentication();
        Member otherMember = Member.builder()
                .email("other@test.com")
                .name("다른사용자")
                .build();

        Field otherMemberIdField = otherMember.getClass().getDeclaredField("id");
        otherMemberIdField.setAccessible(true);
        otherMemberIdField.set(otherMember, 2L);

        Project otherProject = Project.builder()
                .member(otherMember)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .projectName("다른 프로젝트")
                .shortDescription("다른 설명")
                .techStack(Arrays.asList("Python", "Django"))
                .teamComposition("다른 팀 구성")
                .detailedDescription("다른 상세 설명")
                .build();

        Field otherProjectIdField = otherProject.getClass().getDeclaredField("id");
        otherProjectIdField.setAccessible(true);
        otherProjectIdField.set(otherProject, 1L);

        given(projectRepository.findById(1L)).willReturn(Optional.of(otherProject));

        // when & then
        assertThatThrownBy(() -> projectService.updateProject(1L, updateRequest))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
}