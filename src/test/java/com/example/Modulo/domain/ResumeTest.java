package com.example.Modulo.domain;

import com.example.Modulo.global.enums.OAuthProvider;
import com.example.Modulo.exception.InvalidResumeTitleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResumeTest {
    private static final String TEST_TITLE = "My Resume";
    private static final String EMPTY_TITLE = "";
    private static final String BLANK_TITLE = "   ";

    private Member member;
    private Resume resume;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .name("Test Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        resume = Resume.builder()
                .member(member)
                .title(TEST_TITLE)
                .build();
    }

    @Test
    @DisplayName("Resume 생성 성공 테스트")
    void createResumeSuccessTest() {
        assertThat(resume.getMember()).isEqualTo(member);
        assertThat(resume.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(resume.getSections()).isEmpty();
    }

    @Test
    @DisplayName("빈 제목으로 Resume 생성 실패 테스트")
    void createResumeWithEmptyTitleTest() {
        assertThatThrownBy(() -> Resume.builder()
                .member(member)
                .title(EMPTY_TITLE)
                .build())
                .isInstanceOf(InvalidResumeTitleException.class);
    }

    @Test
    @DisplayName("공백 제목으로 Resume 생성 실패 테스트")
    void createResumeWithBlankTitleTest() {
        assertThatThrownBy(() -> Resume.builder()
                .member(member)
                .title(BLANK_TITLE)
                .build())
                .isInstanceOf(InvalidResumeTitleException.class);
    }

    @Test
    @DisplayName("제목 업데이트 성공 테스트")
    void updateTitleSuccessTest() {
        String newTitle = "Updated Resume";
        resume.updateTitle(newTitle);
        assertThat(resume.getTitle()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("빈 제목으로 업데이트 실패 테스트")
    void updateEmptyTitleTest() {
        assertThatThrownBy(() -> resume.updateTitle(EMPTY_TITLE))
                .isInstanceOf(InvalidResumeTitleException.class);
    }
}