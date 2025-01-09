package com.example.Modulo.service;

import com.example.Modulo.domain.Member;
import com.example.Modulo.domain.Resume;
import com.example.Modulo.domain.ResumeSection;
import com.example.Modulo.domain.SectionContent;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeSectionHandlerTest {

    @InjectMocks
    private ResumeSectionHandler resumeSectionHandler;

    @Mock
    private ResumeRepository resumeRepository;

    @Captor
    private ArgumentCaptor<List<Resume>> resumesCaptor;

    private Member member;
    private Resume resume1, resume2;
    private ResumeSection careerSection1, projectSection1, careerSection2;
    private SectionContent content1, content2, content3;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();
        setId(member, 1L);

        resume1 = Resume.builder()
                .member(member)
                .title("이력서1")
                .build();
        setId(resume1, 1L);

        resume2 = Resume.builder()
                .member(member)
                .title("이력서2")
                .build();
        setId(resume2, 2L);

        careerSection1 = ResumeSection.builder()
                .resume(resume1)
                .orderIndex(1)
                .topMargin(0)
                .sectionType(SectionType.CAREER)
                .build();
        setId(careerSection1, 1L);

        projectSection1 = ResumeSection.builder()
                .resume(resume1)
                .orderIndex(2)
                .topMargin(0)
                .sectionType(SectionType.PROJECT)
                .build();
        setId(projectSection1, 2L);

        careerSection2 = ResumeSection.builder()
                .resume(resume2)
                .orderIndex(1)
                .topMargin(0)
                .sectionType(SectionType.CAREER)
                .build();
        setId(careerSection2, 3L);

        content1 = SectionContent.builder()
                .resumeSection(careerSection1)
                .orderIndex(1)
                .topMargin(0)
                .contentId(100L)
                .build();
        setId(content1, 1L);

        content2 = SectionContent.builder()
                .resumeSection(careerSection1)
                .orderIndex(2)
                .topMargin(0)
                .contentId(200L)
                .build();
        setId(content2, 2L);

        content3 = SectionContent.builder()
                .resumeSection(careerSection2)
                .orderIndex(1)
                .topMargin(0)
                .contentId(100L)
                .build();
        setId(content3, 3L);

        careerSection1.updateContents(Arrays.asList(content1, content2));
        careerSection2.updateContents(Arrays.asList(content3));
        projectSection1.updateContents(new ArrayList<>());

        resume1.updateSections(Arrays.asList(careerSection1, projectSection1));
        resume2.updateSections(Arrays.asList(careerSection2));
    }

    private void setId(Object object, Long id) throws Exception {
        Field idField = object.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(object, id);
    }

    @Test
    @DisplayName("특정 Content 삭제 시 모든 이력서에서 해당 Content가 제거됨")
    void handleContentDeletion_RemovesContentFromAllResumes() {
        // given
        when(resumeRepository.findAll()).thenReturn(Arrays.asList(resume1, resume2));

        // when
        resumeSectionHandler.handleContentDeletion(100L, SectionType.CAREER);

        // then
        verify(resumeRepository).saveAll(resumesCaptor.capture());
        List<Resume> savedResumes = resumesCaptor.getValue();

        assertThat(savedResumes).hasSize(2);

        Resume savedResume1 = savedResumes.stream()
                .filter(r -> r.getId().equals(1L))
                .findFirst()
                .orElseThrow();
        Resume savedResume2 = savedResumes.stream()
                .filter(r -> r.getId().equals(2L))
                .findFirst()
                .orElseThrow();

        ResumeSection savedCareerSection1 = savedResume1.getSections().stream()
                .filter(s -> s.getSectionType() == SectionType.CAREER)
                .findFirst()
                .orElseThrow();
        assertThat(savedCareerSection1.getContents()).hasSize(1);
        assertThat(savedCareerSection1.getContents().get(0).getContentId()).isEqualTo(200L);

        ResumeSection savedCareerSection2 = savedResume2.getSections().stream()
                .filter(s -> s.getSectionType() == SectionType.CAREER)
                .findFirst()
                .orElseThrow();
        assertThat(savedCareerSection2.getContents()).isEmpty();
    }

    @Test
    @DisplayName("다른 타입의 Section은 영향을 받지 않음")
    void handleContentDeletion_DoesNotAffectOtherSectionTypes() {
        // given
        when(resumeRepository.findAll()).thenReturn(Arrays.asList(resume1, resume2));

        // when
        resumeSectionHandler.handleContentDeletion(100L, SectionType.PROJECT);

        // then
        verify(resumeRepository).saveAll(resumesCaptor.capture());
        List<Resume> savedResumes = resumesCaptor.getValue();

        Resume savedResume1 = savedResumes.stream()
                .filter(r -> r.getId().equals(1L))
                .findFirst()
                .orElseThrow();
        Resume savedResume2 = savedResumes.stream()
                .filter(r -> r.getId().equals(2L))
                .findFirst()
                .orElseThrow();

        ResumeSection savedCareerSection1 = savedResume1.getSections().stream()
                .filter(s -> s.getSectionType() == SectionType.CAREER)
                .findFirst()
                .orElseThrow();
        ResumeSection savedCareerSection2 = savedResume2.getSections().stream()
                .filter(s -> s.getSectionType() == SectionType.CAREER)
                .findFirst()
                .orElseThrow();

        assertThat(savedCareerSection1.getContents()).hasSize(2);
        assertThat(savedCareerSection2.getContents()).hasSize(1);
    }

    @Test
    @DisplayName("삭제할 Content가 없는 경우에도 정상 처리됨")
    void handleContentDeletion_HandleNonExistentContent() {
        // given
        when(resumeRepository.findAll()).thenReturn(Arrays.asList(resume1, resume2));

        // when
        resumeSectionHandler.handleContentDeletion(999L, SectionType.CAREER);

        // then
        verify(resumeRepository).saveAll(resumesCaptor.capture());
        List<Resume> savedResumes = resumesCaptor.getValue();

        Resume savedResume1 = savedResumes.stream()
                .filter(r -> r.getId().equals(1L))
                .findFirst()
                .orElseThrow();
        Resume savedResume2 = savedResumes.stream()
                .filter(r -> r.getId().equals(2L))
                .findFirst()
                .orElseThrow();

        assertThat(savedResume1.getSections().get(0).getContents()).hasSize(2);
        assertThat(savedResume2.getSections().get(0).getContents()).hasSize(1);
    }

    @Test
    @DisplayName("이력서가 없는 경우 정상 처리됨")
    void handleContentDeletion_HandleEmptyResumeList() {
        // given
        when(resumeRepository.findAll()).thenReturn(new ArrayList<>());

        // when
        resumeSectionHandler.handleContentDeletion(100L, SectionType.CAREER);

        // then
        verify(resumeRepository).saveAll(resumesCaptor.capture());
        List<Resume> savedResumes = resumesCaptor.getValue();
        assertThat(savedResumes).isEmpty();
    }
}