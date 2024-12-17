package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidProjectDateException;
import com.example.Modulo.exception.InvalidProjectFieldException;
import com.example.Modulo.global.enums.OAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProjectTest {
    private static final YearMonth TEST_START_DATE = YearMonth.of(2020, 3);
    private static final YearMonth TEST_END_DATE = YearMonth.of(2024, 2);
    private static final String TEST_PROJECT_NAME = "Test Project";
    private static final String TEST_SHORT_DESCRIPTION = "Test Description";
    private static final List<String> TEST_TECH_STACK = Arrays.asList("Java", "Spring");
    private static final String TEST_TEAM_COMPOSITION = "Backend 3, Frontend 2";
    private static final String TEST_DETAILED_DESCRIPTION = "Detailed description";

    private Member member;
    private Project project;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .name("Test Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        project = Project.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .projectName(TEST_PROJECT_NAME)
                .shortDescription(TEST_SHORT_DESCRIPTION)
                .techStack(TEST_TECH_STACK)
                .teamComposition(TEST_TEAM_COMPOSITION)
                .detailedDescription(TEST_DETAILED_DESCRIPTION)
                .build();
    }

    @Test
    @DisplayName("Project 생성 테스트")
    void createTest() {
        assertThat(project.getMember()).isEqualTo(member);
        assertThat(project.getStartDate()).isEqualTo(TEST_START_DATE);
        assertThat(project.getEndDate()).isEqualTo(TEST_END_DATE);
        assertThat(project.getProjectName()).isEqualTo(TEST_PROJECT_NAME);
        assertThat(project.getShortDescription()).isEqualTo(TEST_SHORT_DESCRIPTION);
        assertThat(project.getTechStack()).asList().containsExactlyInAnyOrderElementsOf(TEST_TECH_STACK);
        assertThat(project.getTeamComposition()).isEqualTo(TEST_TEAM_COMPOSITION);
        assertThat(project.getDetailedDescription()).isEqualTo(TEST_DETAILED_DESCRIPTION);
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦은 경우 실패")
    void validateDateTest() {
        assertThatThrownBy(() -> Project.builder()
                .member(member)
                .startDate(TEST_END_DATE)
                .endDate(TEST_START_DATE)
                .projectName(TEST_PROJECT_NAME)
                .shortDescription(TEST_SHORT_DESCRIPTION)
                .techStack(TEST_TECH_STACK)
                .teamComposition(TEST_TEAM_COMPOSITION)
                .detailedDescription(TEST_DETAILED_DESCRIPTION)
                .build())
                .isInstanceOf(InvalidProjectDateException.class);
    }

    @Test
    @DisplayName("필수 필드 누락 시 실패")
    void validateFieldsTest() {
        assertThatThrownBy(() -> Project.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .projectName("")
                .shortDescription(TEST_SHORT_DESCRIPTION)
                .techStack(TEST_TECH_STACK)
                .teamComposition(TEST_TEAM_COMPOSITION)
                .detailedDescription(TEST_DETAILED_DESCRIPTION)
                .build())
                .isInstanceOf(InvalidProjectFieldException.class);

        assertThatThrownBy(() -> Project.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .projectName(TEST_PROJECT_NAME)
                .shortDescription(TEST_SHORT_DESCRIPTION)
                .techStack(new ArrayList<>())  // 빈 리스트
                .teamComposition(TEST_TEAM_COMPOSITION)
                .detailedDescription(TEST_DETAILED_DESCRIPTION)
                .build())
                .isInstanceOf(InvalidProjectFieldException.class);
    }

    @Test
    @DisplayName("techStack이 null일 때 생성 실패")
    void createWithNullTechStackTest() {
        assertThatThrownBy(() -> Project.builder()
                .member(member)
                .startDate(TEST_START_DATE)
                .endDate(TEST_END_DATE)
                .projectName(TEST_PROJECT_NAME)
                .shortDescription(TEST_SHORT_DESCRIPTION)
                .techStack(null)
                .teamComposition(TEST_TEAM_COMPOSITION)
                .detailedDescription(TEST_DETAILED_DESCRIPTION)
                .build())
                .isInstanceOf(InvalidProjectFieldException.class);
    }
}