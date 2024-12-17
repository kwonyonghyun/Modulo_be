package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidSelfIntroductionFieldException;
import com.example.Modulo.global.enums.OAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelfIntroductionTest {
    private static final String TEST_TITLE = "자기소개";
    private static final String TEST_CONTENT = "안녕하세요. 저는...";

    private Member member;
    private SelfIntroduction selfIntroduction;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .name("Test Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        selfIntroduction = SelfIntroduction.builder()
                .member(member)
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .build();
    }

    @Test
    @DisplayName("SelfIntroduction 생성 테스트")
    void createTest() {
        assertThat(selfIntroduction.getMember()).isEqualTo(member);
        assertThat(selfIntroduction.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(selfIntroduction.getContent()).isEqualTo(TEST_CONTENT);
    }

    @Test
    @DisplayName("SelfIntroduction 업데이트 테스트")
    void updateTest() {
        // given
        String newTitle = "새로운 자기소개";
        String newContent = "새로운 내용입니다.";

        // when
        selfIntroduction.update(newTitle, newContent);

        // then
        assertThat(selfIntroduction.getTitle()).isEqualTo(newTitle);
        assertThat(selfIntroduction.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("필수 필드 누락 시 생성 실패")
    void validateFieldsTest() {
        assertThatThrownBy(() -> SelfIntroduction.builder()
                .member(member)
                .title("")
                .content(TEST_CONTENT)
                .build())
                .isInstanceOf(InvalidSelfIntroductionFieldException.class);

        assertThatThrownBy(() -> SelfIntroduction.builder()
                .member(member)
                .title(TEST_TITLE)
                .content(null)
                .build())
                .isInstanceOf(InvalidSelfIntroductionFieldException.class);
    }

    @Test
    @DisplayName("필수 필드 누락 시 업데이트 실패")
    void validateFieldsUpdateTest() {
        assertThatThrownBy(() -> selfIntroduction.update("", TEST_CONTENT))
                .isInstanceOf(InvalidSelfIntroductionFieldException.class);

        assertThatThrownBy(() -> selfIntroduction.update(TEST_TITLE, ""))
                .isInstanceOf(InvalidSelfIntroductionFieldException.class);
    }
}