package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidEducationDateException;
import com.example.Modulo.exception.InvalidEducationFieldException;
import com.example.Modulo.global.enums.EducationLevel;
import com.example.Modulo.global.enums.OAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EducationTest {
    private static final YearMonth TEST_START_DATE = YearMonth.of(2020, 3);
    private static final YearMonth TEST_END_DATE = YearMonth.of(2024, 2);
    private static final String TEST_SCHOOL = "Test University";
    private static final String TEST_MAJOR = "Computer Science";
    private static final EducationLevel TEST_EDUCATION_LEVEL = EducationLevel.MASTER;

    private static final YearMonth INVALID_START_DATE = YearMonth.of(2024, 3);
    private static final YearMonth INVALID_END_DATE = YearMonth.of(2020, 2);

    private Member member;
    private Education education;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .name("Test Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        education = Education.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .school(TEST_SCHOOL)
                .major(TEST_MAJOR)
                .educationLevel(TEST_EDUCATION_LEVEL)
                .build();
    }

    @Test
    @DisplayName("Education 생성 테스트")
    void createTest() {
        assertThat(education.getMember()).isEqualTo(member);
        assertThat(education.getStartDate()).isEqualTo(TEST_START_DATE);
        assertThat(education.getEndDate()).isEqualTo(TEST_END_DATE);
        assertThat(education.getSchool()).isEqualTo(TEST_SCHOOL);
        assertThat(education.getMajor()).isEqualTo(TEST_MAJOR);
        assertThat(education.getEducationLevel()).isEqualTo(TEST_EDUCATION_LEVEL);
    }

    @Test
    @DisplayName("Education major null 허용 테스트")
    void createWithNullMajorTest() {
        Education educationWithNullMajor = Education.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .school(TEST_SCHOOL)
                .major(null)
                .educationLevel(TEST_EDUCATION_LEVEL)
                .build();

        assertThat(educationWithNullMajor.getMajor()).isNull();
    }

    @Test
    @DisplayName("Education 업데이트 테스트")
    void updateTest() {
        YearMonth newStartDate = YearMonth.of(2021, 3);
        YearMonth newEndDate = YearMonth.of(2025, 2);
        String newSchool = "New University";
        String newMajor = "Software Engineering";
        EducationLevel newLevel = EducationLevel.MASTER;

        education.update(newStartDate, newEndDate, newSchool, newMajor, newLevel);

        assertThat(education.getStartDate()).isEqualTo(newStartDate);
        assertThat(education.getEndDate()).isEqualTo(newEndDate);
        assertThat(education.getSchool()).isEqualTo(newSchool);
        assertThat(education.getMajor()).isEqualTo(newMajor);
        assertThat(education.getEducationLevel()).isEqualTo(newLevel);
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦은 경우 실패")
    void validateDateTest() {
        assertThatThrownBy(() -> Education.builder()
                .member(member)
                .startDate(INVALID_START_DATE)
                .endDate(INVALID_END_DATE)
                .school(TEST_SCHOOL)
                .major(TEST_MAJOR)
                .educationLevel(TEST_EDUCATION_LEVEL)
                .build())
                .isInstanceOf(InvalidEducationDateException.class);
    }

    @Test
    @DisplayName("필수 필드 누락 시 실패")
    void validateFieldsTest() {
        assertThatThrownBy(() -> Education.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .school("")
                .major(TEST_MAJOR)
                .educationLevel(TEST_EDUCATION_LEVEL)
                .build())
                .isInstanceOf(InvalidEducationFieldException.class);

        assertThatThrownBy(() -> Education.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .school(TEST_SCHOOL)
                .major(TEST_MAJOR)
                .educationLevel(null)
                .build())
                .isInstanceOf(InvalidEducationFieldException.class);
    }

    @Test
    @DisplayName("major가 빈 문자열일 때 생성 테스트")
    void createWithEmptyMajorTest() {
        Education educationWithEmptyMajor = Education.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .school(TEST_SCHOOL)
                .major("")
                .educationLevel(TEST_EDUCATION_LEVEL)
                .build();

        assertThat(educationWithEmptyMajor.getMajor()).isEqualTo("");
    }

    @Test
    @DisplayName("날짜가 같을 때 생성 테스트")
    void createWithSameDateTest() {
        YearMonth sameDate = YearMonth.of(2020, 3);
        Education education = Education.builder()
                .member(member)
                .startDate(sameDate)
                .endDate(sameDate)
                .school(TEST_SCHOOL)
                .major(TEST_MAJOR)
                .educationLevel(TEST_EDUCATION_LEVEL)
                .build();

        assertThat(education.getStartDate()).isEqualTo(education.getEndDate());
    }
}