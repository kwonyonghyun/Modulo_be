package com.example.Modulo.domain;

import com.example.Modulo.global.enums.OAuthProvider;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.exception.InvalidSectionContentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionContentTest {
    private static final Integer ORDER_INDEX = 1;
    private static final Integer TOP_MARGIN = 10;
    private static final Long CONTENT_ID = 1L;
    private static final Integer NEGATIVE_ORDER = -1;
    private static final Integer NEGATIVE_MARGIN = -10;

    private Member member;
    private Resume resume;
    private ResumeSection resumeSection;
    private SectionContent sectionContent;

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

        sectionContent = SectionContent.builder()
                .resumeSection(resumeSection)
                .orderIndex(ORDER_INDEX)
                .topMargin(TOP_MARGIN)
                .contentId(CONTENT_ID)
                .build();
    }

    @Test
    @DisplayName("SectionContent 생성 성공 테스트")
    void createSectionContentSuccessTest() {
        assertThat(sectionContent.getResumeSection()).isEqualTo(resumeSection);
        assertThat(sectionContent.getOrderIndex()).isEqualTo(ORDER_INDEX);
        assertThat(sectionContent.getTopMargin()).isEqualTo(TOP_MARGIN);
        assertThat(sectionContent.getContentId()).isEqualTo(CONTENT_ID);
    }

    @Test
    @DisplayName("음수 orderIndex로 SectionContent 생성 실패 테스트")
    void createSectionContentWithNegativeOrderTest() {
        assertThatThrownBy(() -> SectionContent.builder()
                .resumeSection(resumeSection)
                .orderIndex(NEGATIVE_ORDER)
                .topMargin(TOP_MARGIN)
                .contentId(CONTENT_ID)
                .build())
                .isInstanceOf(InvalidSectionContentException.class);
    }

    @Test
    @DisplayName("음수 topMargin으로 SectionContent 생성 실패 테스트")
    void createSectionContentWithNegativeMarginTest() {
        assertThatThrownBy(() -> SectionContent.builder()
                .resumeSection(resumeSection)
                .orderIndex(ORDER_INDEX)
                .topMargin(NEGATIVE_MARGIN)
                .contentId(CONTENT_ID)
                .build())
                .isInstanceOf(InvalidSectionContentException.class);
    }

    @Test
    @DisplayName("컨텐츠 업데이트 성공 테스트")
    void updateContentSuccessTest() {
        Integer newOrder = 2;
        Integer newMargin = 20;

        sectionContent.update(newOrder, newMargin);

        assertThat(sectionContent.getOrderIndex()).isEqualTo(newOrder);
        assertThat(sectionContent.getTopMargin()).isEqualTo(newMargin);
    }

    @Test
    @DisplayName("setResumeSection null 테스트")
    void setResumeSectionNullTest() {
        sectionContent.setResumeSection(null);
        assertThat(sectionContent.getResumeSection()).isNull();
    }
}