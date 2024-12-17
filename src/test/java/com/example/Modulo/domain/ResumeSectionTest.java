package com.example.Modulo.domain;

import com.example.Modulo.global.enums.OAuthProvider;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.exception.InvalidResumeSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResumeSectionTest {
    private static final Integer ORDER_INDEX = 1;
    private static final Integer TOP_MARGIN = 10;
    private static final Integer NEGATIVE_ORDER = -1;
    private static final Integer NEGATIVE_MARGIN = -10;

    private Member member;
    private Resume resume;
    private ResumeSection resumeSection;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .name("Test Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        resume = Resume.builder()
                .member(member)
                .title("Test Resume")
                .build();

        resumeSection = ResumeSection.builder()
                .resume(resume)
                .orderIndex(ORDER_INDEX)
                .topMargin(TOP_MARGIN)
                .sectionType(SectionType.BASIC_INFO)
                .build();
    }

    @Test
    @DisplayName("ResumeSection 생성 성공 테스트")
    void createResumeSectionSuccessTest() {
        assertThat(resumeSection.getResume()).isEqualTo(resume);
        assertThat(resumeSection.getOrderIndex()).isEqualTo(ORDER_INDEX);
        assertThat(resumeSection.getTopMargin()).isEqualTo(TOP_MARGIN);
        assertThat(resumeSection.getSectionType()).isEqualTo(SectionType.BASIC_INFO);
        assertThat(resumeSection.getContents()).isEmpty();
    }

    @Test
    @DisplayName("음수 orderIndex로 ResumeSection 생성 실패 테스트")
    void createResumeSectionWithNegativeOrderTest() {
        assertThatThrownBy(() -> ResumeSection.builder()
                .resume(resume)
                .orderIndex(NEGATIVE_ORDER)
                .topMargin(TOP_MARGIN)
                .sectionType(SectionType.BASIC_INFO)
                .build())
                .isInstanceOf(InvalidResumeSectionException.class);
    }

    @Test
    @DisplayName("음수 topMargin으로 ResumeSection 생성 실패 테스트")
    void createResumeSectionWithNegativeMarginTest() {
        assertThatThrownBy(() -> ResumeSection.builder()
                .resume(resume)
                .orderIndex(ORDER_INDEX)
                .topMargin(NEGATIVE_MARGIN)
                .sectionType(SectionType.BASIC_INFO)
                .build())
                .isInstanceOf(InvalidResumeSectionException.class);
    }

    @Test
    @DisplayName("섹션 업데이트 성공 테스트")
    void updateSectionSuccessTest() {
        Integer newOrder = 2;
        Integer newMargin = 20;

        resumeSection.update(newOrder, newMargin);

        assertThat(resumeSection.getOrderIndex()).isEqualTo(newOrder);
        assertThat(resumeSection.getTopMargin()).isEqualTo(newMargin);
    }

    @Test
    @DisplayName("setResume null 테스트")
    void setResumeNullTest() {
        resumeSection.setResume(null);
        assertThat(resumeSection.getResume()).isNull();
    }

    @Test
    @DisplayName("null contents로 업데이트시 빈 리스트로 처리")
    void updateNullContentsTest() {
        resumeSection.updateContents(null);
        assertThat(resumeSection.getContents()).isEmpty();
    }
}