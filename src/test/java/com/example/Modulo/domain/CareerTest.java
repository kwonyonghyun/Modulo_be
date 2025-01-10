package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidCareerDateException;
import com.example.Modulo.exception.InvalidCareerFieldException;
import com.example.Modulo.global.enums.OAuthProvider;
import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CareerTest {
    private static final YearMonth TEST_START_DATE = YearMonth.of(2020, 1);
    private static final YearMonth TEST_END_DATE = YearMonth.of(2023, 12);
    private static final String TEST_COMPANY_NAME = "Test Company";
    private static final String TEST_COMPANY_DESCRIPTION = "Test Company Description";
    private static final String TEST_POSITION = "Software Engineer";
    private static final List<String> TEST_TECH_STACK = Arrays.asList("Java", "Spring", "JPA");
    private static final String TEST_ACHIEVEMENTS = "Test Achievements";

    private static final YearMonth INVALID_START_DATE = YearMonth.of(2024, 1);
    private static final YearMonth INVALID_END_DATE = YearMonth.of(2023, 1);

    private Member member;
    private Career career;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .name("Test Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        career = Career.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .companyName(TEST_COMPANY_NAME)
                .companyDescription(TEST_COMPANY_DESCRIPTION)
                .position(TEST_POSITION)
                .techStack(TEST_TECH_STACK)
                .achievements(TEST_ACHIEVEMENTS)
                .build();
    }

    @Test
    @DisplayName("Career 생성 테스트")
    void createTest() {
        assertThat(career.getMember()).isEqualTo(member);
        assertThat(career.getStartDate()).isEqualTo(TEST_START_DATE);
        assertThat(career.getEndDate()).isEqualTo(TEST_END_DATE);
        assertThat(career.getCompanyName()).isEqualTo(TEST_COMPANY_NAME);
        assertThat(career.getCompanyDescription()).isEqualTo(TEST_COMPANY_DESCRIPTION);
        assertThat(career.getPosition()).isEqualTo(TEST_POSITION);
        assertThat(career.getTechStack()).containsExactlyElementsOf(TEST_TECH_STACK);
        assertThat(career.getAchievements()).isEqualTo(TEST_ACHIEVEMENTS);
    }

    @Test
    @DisplayName("Career 업데이트 테스트")
    void updateTest() {
        YearMonth newStartDate = YearMonth.of(2021, 1);
        YearMonth newEndDate = YearMonth.of(2023, 12);
        String newCompanyName = "New Company";
        String newDescription = "New Description";
        String newPosition = "Senior Engineer";
        List<String> newTechStack = Arrays.asList("Python", "Django");
        String newAchievements = "New Achievements";

        career.update(newStartDate, newEndDate, newCompanyName, newDescription,
                newPosition, newTechStack, newAchievements);

        assertThat(career.getStartDate()).isEqualTo(newStartDate);
        assertThat(career.getEndDate()).isEqualTo(newEndDate);
        assertThat(career.getCompanyName()).isEqualTo(newCompanyName);
        assertThat(career.getCompanyDescription()).isEqualTo(newDescription);
        assertThat(career.getPosition()).isEqualTo(newPosition);
        assertThat(career.getTechStack()).containsExactlyElementsOf(newTechStack);
        assertThat(career.getAchievements()).isEqualTo(newAchievements);
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦은 경우 생성 실패")
    void createWithInvalidDateTest() {
        BusinessException exception = assertThrows(InvalidCareerDateException.class, () ->
                Career.builder()
                        .member(member)
                        .startDate(INVALID_START_DATE)
                        .endDate(INVALID_END_DATE)
                        .companyName(TEST_COMPANY_NAME)
                        .companyDescription(TEST_COMPANY_DESCRIPTION)
                        .position(TEST_POSITION)
                        .techStack(TEST_TECH_STACK)
                        .achievements(TEST_ACHIEVEMENTS)
                        .build()
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CAREER_DATE);
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦은 경우 업데이트 실패")
    void updateWithInvalidDateTest() {
        BusinessException exception = assertThrows(InvalidCareerDateException.class, () ->
                career.update(INVALID_START_DATE, INVALID_END_DATE, TEST_COMPANY_NAME,
                        TEST_COMPANY_DESCRIPTION, TEST_POSITION, TEST_TECH_STACK, TEST_ACHIEVEMENTS)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CAREER_DATE);
    }

    @Test
    @DisplayName("null 날짜로 생성 실패")
    void createWithNullDateTest() {
        assertThatThrownBy(() -> Career.builder()
                .member(member)
                .startDate(null)
                .endDate(TEST_END_DATE)
                .companyName(TEST_COMPANY_NAME)
                .companyDescription(TEST_COMPANY_DESCRIPTION)
                .position(TEST_POSITION)
                .techStack(TEST_TECH_STACK)
                .achievements(TEST_ACHIEVEMENTS)
                .build())
                .isInstanceOf(InvalidCareerDateException.class);
    }

    @Test
    @DisplayName("techStack이 null일 때 빈 리스트로 초기화")
    void createWithNullTechStackTest() {
        Career careerWithNullTech = Career.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .companyName(TEST_COMPANY_NAME)
                .companyDescription(TEST_COMPANY_DESCRIPTION)
                .position(TEST_POSITION)
                .techStack(null)
                .achievements(TEST_ACHIEVEMENTS)
                .build();

        assertThat(careerWithNullTech.getTechStack()).isNotNull();
        assertThat(careerWithNullTech.getTechStack()).isEmpty();
    }

    @Test
    @DisplayName("null 날짜로 업데이트 실패")
    void updateWithNullDateTest() {
        assertThatThrownBy(() -> career.update(
                null,
                TEST_END_DATE,
                TEST_COMPANY_NAME,
                TEST_COMPANY_DESCRIPTION,
                TEST_POSITION,
                TEST_TECH_STACK,
                TEST_ACHIEVEMENTS
        )).isInstanceOf(InvalidCareerDateException.class);
    }

    @Test
    @DisplayName("techStack이 빈 리스트일 때 업데이트 테스트")
    void updateWithEmptyTechStackTest() {
        career.update(
                TEST_START_DATE,
                TEST_END_DATE,
                TEST_COMPANY_NAME,
                TEST_COMPANY_DESCRIPTION,
                TEST_POSITION,
                new ArrayList<>(),
                TEST_ACHIEVEMENTS
        );
        assertThat(career.getTechStack()).isEmpty();
    }
}