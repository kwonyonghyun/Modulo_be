package com.example.Modulo.service;

import com.example.Modulo.domain.Member;
import com.example.Modulo.domain.Resume;
import com.example.Modulo.dto.request.ResumeCreateRequest;
import com.example.Modulo.dto.request.ResumeUpdateRequest;
import com.example.Modulo.dto.request.ResumeSectionRequest;
import com.example.Modulo.dto.request.SectionContentRequest;
import com.example.Modulo.dto.response.BasicInfoResponse;
import com.example.Modulo.dto.response.ResumeDetailResponse;
import com.example.Modulo.dto.response.ResumeResponse;
import com.example.Modulo.exception.InvalidSectionContentException;
import com.example.Modulo.exception.ResumeNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.MemberRepository;
import com.example.Modulo.repository.ResumeRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @InjectMocks
    private ResumeService resumeService;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BasicInfoService basicInfoService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Member member;
    private Resume resume;
    private ResumeCreateRequest createRequest;
    private ResumeUpdateRequest updateRequest;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();

        Field memberIdField = member.getClass().getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(member, 1L);

        resume = Resume.builder()
                .member(member)
                .title("테스트 이력서")
                .build();

        Field resumeIdField = resume.getClass().getDeclaredField("id");
        resumeIdField.setAccessible(true);
        resumeIdField.set(resume, 1L);

        List<ResumeSectionRequest> sections = new ArrayList<>();
        List<SectionContentRequest> contents = new ArrayList<>();
        contents.add(createSectionContentRequest(1, 10, 1L));
        sections.add(createResumeSectionRequest(1, 20, SectionType.BASIC_INFO, contents));

        createRequest = new ResumeCreateRequest();
        setFieldValue(createRequest, "title", "테스트 이력서");
        setFieldValue(createRequest, "sections", sections);

        updateRequest = new ResumeUpdateRequest();
        setFieldValue(updateRequest, "title", "수정된 이력서");
        setFieldValue(updateRequest, "sections", sections);

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("1");
    }

    @Test
    @DisplayName("이력서 생성 성공")
    void createResume_Success() throws Exception {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(resumeRepository.save(any(Resume.class))).willReturn(resume);
        given(basicInfoService.getBasicInfoById(1L))
                .willReturn(BasicInfoResponse.builder()
                        .id(1L)
                        .name("테스터")
                        .email("test@test.com")
                        .build());

        // when
        Long resumeId = resumeService.createResume(createRequest);

        // then
        assertThat(resumeId).isEqualTo(1L);
        verify(resumeRepository).save(any(Resume.class));
    }

    @Test
    @DisplayName("이력서 수정 성공")
    void updateResume_Success() throws Exception {
        // given
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));
        given(basicInfoService.getBasicInfoById(1L))
                .willReturn(BasicInfoResponse.builder()
                        .id(1L)
                        .name("테스터")
                        .email("test@test.com")
                        .build());

        // when
        resumeService.updateResume(1L, updateRequest);

        // then
        assertThat(resume.getTitle()).isEqualTo(updateRequest.getTitle());
    }

    @Test
    @DisplayName("이력서 목록 조회 성공")
    void getMyResumes_Success() {
        // given
        given(resumeRepository.findAllByMemberId(1L)).willReturn(List.of(resume));

        // when
        List<ResumeResponse> result = resumeService.getMyResumes();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(resume.getId());
        assertThat(result.get(0).getTitle()).isEqualTo(resume.getTitle());
    }

    @Test
    @DisplayName("이력서 단건 조회 성공")
    void getResumeById_Success() {
        // given
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));

        // when
        ResumeResponse result = resumeService.getResumeById(1L);

        // then
        assertThat(result.getId()).isEqualTo(resume.getId());
        assertThat(result.getTitle()).isEqualTo(resume.getTitle());
    }

    @Test
    @DisplayName("이력서 삭제 성공")
    void deleteResume_Success() {
        // given
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));

        // when
        resumeService.deleteResume(1L);

        // then
        verify(resumeRepository).delete(resume);
    }

    @Test
    @DisplayName("존재하지 않는 이력서 조회 시 예외 발생")
    void getResumeById_NotFound() {
        // given
        given(resumeRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> resumeService.getResumeById(1L))
                .isInstanceOf(ResumeNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 이력서 수정 시 예외 발생")
    void updateResume_NotFound() {
        // given
        given(resumeRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> resumeService.updateResume(1L, updateRequest))
                .isInstanceOf(ResumeNotFoundException.class);
    }

    @Test
    @DisplayName("권한 없는 이력서 접근 시 예외 발생")
    void updateResume_Unauthorized() throws Exception {
        // given
        Member otherMember = Member.builder()
                .email("other@test.com")
                .name("다른사용자")
                .build();

        Field otherMemberIdField = otherMember.getClass().getDeclaredField("id");
        otherMemberIdField.setAccessible(true);
        otherMemberIdField.set(otherMember, 2L);

        Resume otherResume = Resume.builder()
                .member(otherMember)
                .title("다른 이력서")
                .build();

        Field otherResumeIdField = otherResume.getClass().getDeclaredField("id");
        otherResumeIdField.setAccessible(true);
        otherResumeIdField.set(otherResume, 1L);

        given(resumeRepository.findById(1L)).willReturn(Optional.of(otherResume));

        // when & then
        assertThatThrownBy(() -> resumeService.updateResume(1L, updateRequest))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("존재하지 않는 섹션 콘텐츠로 이력서 생성 시 예외 발생")
    void createResume_InvalidContent() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(basicInfoService.getBasicInfoById(1L)).willThrow(new RuntimeException());

        // when & then
        assertThatThrownBy(() -> resumeService.createResume(createRequest))
                .isInstanceOf(InvalidSectionContentException.class);
    }

    private ResumeSectionRequest createResumeSectionRequest(int orderIndex, int topMargin,
                                                            SectionType sectionType, List<SectionContentRequest> contents) throws Exception {
        ResumeSectionRequest request = new ResumeSectionRequest();
        setFieldValue(request, "orderIndex", orderIndex);
        setFieldValue(request, "topMargin", topMargin);
        setFieldValue(request, "sectionType", sectionType);
        setFieldValue(request, "contents", contents);
        return request;
    }

    private SectionContentRequest createSectionContentRequest(int orderIndex, int topMargin,
                                                              Long contentId) throws Exception {
        SectionContentRequest request = new SectionContentRequest();
        setFieldValue(request, "orderIndex", orderIndex);
        setFieldValue(request, "topMargin", topMargin);
        setFieldValue(request, "contentId", contentId);
        return request;
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}