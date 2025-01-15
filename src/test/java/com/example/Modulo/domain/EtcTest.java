package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidEtcFieldException;
import com.example.Modulo.global.enums.EtcType;
import com.example.Modulo.global.enums.OAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class EtcTest {
    private static final YearMonth TEST_START_DATE = YearMonth.of(2020, 3);
    private static final YearMonth TEST_END_DATE = YearMonth.of(2024, 2);
    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final EtcType TEST_TYPE = EtcType.CERTIFICATE;
    private static final String TEST_ORGANIZATION = "Test Organization";
    private static final String TEST_SCORE = "Pass";

    private Member member;
    private Etc etc;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .name("Test Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        etc = Etc.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .type(TEST_TYPE)
                .organization(TEST_ORGANIZATION)
                .score(TEST_SCORE)
                .build();
    }

    @Test
    @DisplayName("Etc 생성 테스트")
    void createTest() {
        assertThat(etc.getMember()).isEqualTo(member);
        assertThat(etc.getStartDate()).isEqualTo(TEST_START_DATE);
        assertThat(etc.getEndDate()).isEqualTo(TEST_END_DATE);
        assertThat(etc.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(etc.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(etc.getType()).isEqualTo(TEST_TYPE);
        assertThat(etc.getOrganization()).isEqualTo(TEST_ORGANIZATION);
        assertThat(etc.getScore()).isEqualTo(TEST_SCORE);
    }

    @Test
    @DisplayName("Etc Organization과 Score는 null 허용")
    void createWithNullableFieldsTest() {
        Etc etcWithNulls = Etc.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .type(TEST_TYPE)
                .organization(null)
                .score(null)
                .build();

        assertThat(etcWithNulls.getOrganization()).isNull();
        assertThat(etcWithNulls.getScore()).isNull();
    }

    @Test
    @DisplayName("필수 필드 누락 시 실패")
    void validateFieldsTest() {
        assertThatThrownBy(() -> Etc.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .title("")
                .description(TEST_DESCRIPTION)
                .type(TEST_TYPE)
                .build())
                .isInstanceOf(InvalidEtcFieldException.class);
    }

    @Test
    @DisplayName("endDate가 null일 때 생성 테스트")
    void createWithNullEndDateTest() {
        Etc etcWithNullEndDate = Etc.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(null)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .type(TEST_TYPE)
                .build();

        assertThat(etcWithNullEndDate.getEndDate()).isNull();
    }

    @Test
    @DisplayName("organization과 score가 빈 문자열일 때 생성 테스트")
    void createWithEmptyOptionalFieldsTest() {
        Etc etcWithEmptyFields = Etc.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .type(TEST_TYPE)
                .organization("")
                .score("")
                .build();

        assertThat(etcWithEmptyFields.getOrganization()).isEqualTo("");
        assertThat(etcWithEmptyFields.getScore()).isEqualTo("");
    }
}

