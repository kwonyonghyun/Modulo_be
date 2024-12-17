package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidUrlException;
import com.example.Modulo.global.enums.CareerYear;
import com.example.Modulo.global.enums.OAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LinkTest {
    private static final String TEST_TITLE = "GitHub";
    private static final String TEST_URL = "https://github.com";
    private static final String HTTP_URL = "http://github.com";
    private static final String INVALID_URL = "invalid-url";

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test Name";
    private static final Integer TEST_BIRTH_YEAR = 1990;
    private static final String TEST_JOB_POSITION = "Developer";
    private static final String TEST_PROFILE_IMAGE_URL = "http://example.com/image.jpg";
    private static final String TEST_SHORT_BIO = "Hello World";

    private Member member;
    private BasicInfo basicInfo;
    private Link link;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("member@example.com")
                .name("Member Name")
                .provider(OAuthProvider.GOOGLE)
                .build();

        basicInfo = BasicInfo.builder()
                .email(TEST_EMAIL)
                .name(TEST_NAME)
                .birthYear(TEST_BIRTH_YEAR)
                .careerYear(CareerYear.YEAR_2)
                .jobPosition(TEST_JOB_POSITION)
                .profileImageUrl(TEST_PROFILE_IMAGE_URL)
                .shortBio(TEST_SHORT_BIO)
                .member(member)
                .build();

        link = Link.builder()
                .title(TEST_TITLE)
                .url(TEST_URL)
                .build();
    }

    @Test
    @DisplayName("Link 생성 테스트")
    void createTest() {
        assertThat(link.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(link.getUrl()).isEqualTo(TEST_URL);
        assertThat(link.getBasicInfo()).isNull();
    }

    @Test
    @DisplayName("BasicInfo 설정 테스트")
    void setBasicInfoTest() {
        link.setBasicInfo(basicInfo);

        assertThat(link.getBasicInfo()).isEqualTo(basicInfo);
    }

    @Test
    @DisplayName("http URL로 Link 생성 성공")
    void createWithHttpUrlTest() {
        Link httpLink = Link.builder()
                .title(TEST_TITLE)
                .url(HTTP_URL)
                .build();

        assertThat(httpLink.getUrl()).isEqualTo(HTTP_URL);
    }

    @Test
    @DisplayName("잘못된 URL 형식으로 Link 생성 실패")
    void createWithInvalidUrlTest() {
        assertThatThrownBy(() -> Link.builder()
                .title(TEST_TITLE)
                .url(INVALID_URL)
                .build())
                .isInstanceOf(InvalidUrlException.class);
    }

    @Test
    @DisplayName("null URL로 Link 생성 실패")
    void createWithNullUrlTest() {
        assertThatThrownBy(() -> Link.builder()
                .title(TEST_TITLE)
                .url(null)
                .build())
                .isInstanceOf(InvalidUrlException.class);
    }
}