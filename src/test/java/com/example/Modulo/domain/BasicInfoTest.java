package com.example.Modulo.domain;

import com.example.Modulo.global.enums.CareerYear;
import com.example.Modulo.global.enums.OAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BasicInfoTest {
    private static final String TEST_PROFILE_IMAGE_URL = "http://example.com/image.jpg";
    private static final String TEST_NAME = "Test Name";
    private static final String TEST_EMAIL = "test@example.com";
    private static final CareerYear TEST_CAREER_YEAR = CareerYear.YEAR_2;
    private static final Integer TEST_BIRTH_YEAR = 1990;
    private static final String TEST_JOB_POSITION = "Software Engineer";
    private static final String TEST_SHORT_BIO = "Hello, I'm a developer";

    private static final String UPDATE_PROFILE_IMAGE_URL = "http://example.com/new-image.jpg";
    private static final String UPDATE_NAME = "Updated Name";
    private static final String UPDATE_EMAIL = "update@example.com";
    private static final CareerYear UPDATE_CAREER_YEAR = CareerYear.YEAR_5;
    private static final Integer UPDATE_BIRTH_YEAR = 1995;
    private static final String UPDATE_JOB_POSITION = "Senior Engineer";
    private static final String UPDATE_SHORT_BIO = "Updated bio";

    private static final List<String> TEST_TECH_STACK = Arrays.asList("Java", "Spring", "JPA");
    private static final List<String> UPDATE_TECH_STACK = Arrays.asList("Python", "Django", "React");

    private Member member;
    private BasicInfo basicInfo;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("member@example.com")
                .name("Member Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        basicInfo = BasicInfo.builder()
                .member(member)
                .profileImageUrl(TEST_PROFILE_IMAGE_URL)
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .careerYear(TEST_CAREER_YEAR)
                .birthYear(TEST_BIRTH_YEAR)
                .jobPosition(TEST_JOB_POSITION)
                .shortBio(TEST_SHORT_BIO)
                .build();
    }

    @Test
    @DisplayName("BasicInfo 생성 테스트")
    void createTest() {
        assertThat(basicInfo.getMember()).isEqualTo(member);
        assertThat(basicInfo.getProfileImageUrl()).isEqualTo(TEST_PROFILE_IMAGE_URL);
        assertThat(basicInfo.getName()).isEqualTo(TEST_NAME);
        assertThat(basicInfo.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(basicInfo.getCareerYear()).isEqualTo(TEST_CAREER_YEAR);
        assertThat(basicInfo.getBirthYear()).isEqualTo(TEST_BIRTH_YEAR);
        assertThat(basicInfo.getJobPosition()).isEqualTo(TEST_JOB_POSITION);
        assertThat(basicInfo.getShortBio()).isEqualTo(TEST_SHORT_BIO);
    }

    @Test
    @DisplayName("BasicInfo 업데이트 테스트")
    void updateTest() {
        basicInfo.update(
                UPDATE_PROFILE_IMAGE_URL,
                UPDATE_NAME,
                UPDATE_EMAIL,
                UPDATE_CAREER_YEAR,
                UPDATE_BIRTH_YEAR,
                UPDATE_JOB_POSITION,
                UPDATE_SHORT_BIO
        );

        assertThat(basicInfo.getProfileImageUrl()).isEqualTo(UPDATE_PROFILE_IMAGE_URL);
        assertThat(basicInfo.getName()).isEqualTo(UPDATE_NAME);
        assertThat(basicInfo.getEmail()).isEqualTo(UPDATE_EMAIL);
        assertThat(basicInfo.getCareerYear()).isEqualTo(UPDATE_CAREER_YEAR);
        assertThat(basicInfo.getBirthYear()).isEqualTo(UPDATE_BIRTH_YEAR);
        assertThat(basicInfo.getJobPosition()).isEqualTo(UPDATE_JOB_POSITION);
        assertThat(basicInfo.getShortBio()).isEqualTo(UPDATE_SHORT_BIO);
    }

    @Test
    @DisplayName("Links 업데이트 테스트")
    void updateLinksTest() {
        List<Link> newLinks = Arrays.asList(
                Link.builder().title("GitHub").url("https://github.com").build(),
                Link.builder().title("LinkedIn").url("https://linkedin.com").build()
        );

        basicInfo.updateLinks(newLinks);

        assertThat(basicInfo.getLinks()).hasSize(2);
        assertThat(basicInfo.getLinks().get(0).getTitle()).isEqualTo("GitHub");
        assertThat(basicInfo.getLinks().get(1).getTitle()).isEqualTo("LinkedIn");
        assertThat(basicInfo.getLinks()).allMatch(link -> link.getBasicInfo() == basicInfo);
    }

    @Test
    @DisplayName("TechStack 업데이트 테스트")
    void updateTechStackTest() {
        basicInfo.updateTechStack(TEST_TECH_STACK);
        assertThat(basicInfo.getTechStack()).containsExactlyElementsOf(TEST_TECH_STACK);

        basicInfo.updateTechStack(UPDATE_TECH_STACK);
        assertThat(basicInfo.getTechStack()).containsExactlyElementsOf(UPDATE_TECH_STACK);
    }

    @Test
    @DisplayName("null links로 업데이트시 빈 리스트로 처리")
    void updateNullLinksTest() {
        basicInfo.updateLinks(null);
        assertThat(basicInfo.getLinks()).isEmpty();
    }

    @Test
    @DisplayName("null techStack으로 업데이트시 빈 리스트로 처리")
    void updateNullTechStackTest() {
        basicInfo.updateTechStack(null);
        assertThat(basicInfo.getTechStack()).isEmpty();
    }
}