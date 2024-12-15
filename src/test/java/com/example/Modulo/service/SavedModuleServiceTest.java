package com.example.Modulo.service;

import com.example.Modulo.dto.response.*;
import com.example.Modulo.global.enums.CareerYear;
import com.example.Modulo.global.enums.EducationLevel;
import com.example.Modulo.global.enums.EtcType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SavedModuleServiceTest {

    @InjectMocks
    private SavedModuleService savedModuleService;

    @Mock
    private BasicInfoService basicInfoService;

    @Mock
    private CareerService careerService;

    @Mock
    private EducationService educationService;

    @Mock
    private ProjectService projectService;

    @Mock
    private EtcService etcService;

    @Mock
    private SelfIntroductionService selfIntroductionService;

    private BasicInfoResponse basicInfoResponse;
    private CareerResponse careerResponse;
    private EducationResponse educationResponse;
    private ProjectResponse projectResponse;
    private EtcResponse etcResponse;
    private SelfIntroductionResponse selfIntroductionResponse;

    @BeforeEach
    void setUp() {
        BasicInfoResponse.LinkResponse linkResponse = BasicInfoResponse.LinkResponse.builder()
                .id(1L)
                .title("GitHub")
                .url("https://github.com/test")
                .build();

        basicInfoResponse = BasicInfoResponse.builder()
                .id(1L)
                .profileImageUrl("https://example.com/profile.jpg")
                .name("홍길동")
                .email("test@example.com")
                .careerYear(CareerYear.YEAR_1)
                .birthYear(1999)
                .jobPosition("백엔드 개발자")
                .shortBio("열정적인 개발자입니다.")
                .links(List.of(linkResponse))
                .techStack(Arrays.asList("Java", "Spring", "MySQL"))
                .build();

        careerResponse = CareerResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2020, 3))
                .endDate(YearMonth.of(2024, 2))
                .companyName("테스트회사")
                .companyDescription("IT 기업")
                .position("백엔드 개발자")
                .techStack(Arrays.asList("Java", "Spring", "MySQL"))
                .achievements("주요 성과")
                .build();

        educationResponse = EducationResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2018, 3))
                .endDate(YearMonth.of(2022, 2))
                .school("테스트대학교")
                .major("컴퓨터공학")
                .educationLevel(EducationLevel.COLLEGE_2_3)
                .build();

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .projectName("테스트 프로젝트")
                .shortDescription("프로젝트 설명")
                .techStack(Arrays.asList("Java", "Spring", "React"))
                .teamComposition("백엔드 2명, 프론트엔드 2명")
                .detailedDescription("상세 프로젝트 설명")
                .build();

        etcResponse = EtcResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .title("자격증")
                .description("정보처리기사")
                .type(EtcType.CERTIFICATE)
                .organization("한국산업인력공단")
                .score("합격")
                .build();

        selfIntroductionResponse = SelfIntroductionResponse.builder()
                .title("자기소개서")
                .content("저는 열정적인 개발자입니다.")
                .build();
    }

    @Test
    @DisplayName("저장된 모듈 조회 성공")
    void getMySavedModules_Success() {
        // given
        given(basicInfoService.getMyBasicInfos()).willReturn(List.of(basicInfoResponse));
        given(careerService.getMyCareers()).willReturn(List.of(careerResponse));
        given(educationService.getMyEducations()).willReturn(List.of(educationResponse));
        given(projectService.getMyProjects()).willReturn(List.of(projectResponse));
        given(etcService.getMyEtcs()).willReturn(List.of(etcResponse));
        given(selfIntroductionService.getMySelfIntroductions()).willReturn(List.of(selfIntroductionResponse));

        // when
        SavedModuleResponse result = savedModuleService.getMySavedModules();

        // then
        assertThat(result.getBasicInfos()).hasSize(1);
        assertThat(result.getCareers()).hasSize(1);
        assertThat(result.getEducations()).hasSize(1);
        assertThat(result.getProjects()).hasSize(1);
        assertThat(result.getEtcs()).hasSize(1);
        assertThat(result.getSelfIntroductions()).hasSize(1);

        // BasicInfo 검증
        BasicInfoResponse resultBasicInfo = result.getBasicInfos().get(0);
        assertThat(resultBasicInfo.getName()).isEqualTo("홍길동");
        assertThat(resultBasicInfo.getEmail()).isEqualTo("test@example.com");
        assertThat(resultBasicInfo.getCareerYear()).isEqualTo(CareerYear.YEAR_1);
        assertThat(resultBasicInfo.getLinks()).hasSize(1);
        assertThat(resultBasicInfo.getLinks().get(0).getTitle()).isEqualTo("GitHub");

        // Career 검증
        CareerResponse resultCareer = result.getCareers().get(0);
        assertThat(resultCareer.getCompanyName()).isEqualTo("테스트회사");
        assertThat(resultCareer.getPosition()).isEqualTo("백엔드 개발자");
        assertThat(resultCareer.getTechStack()).contains("Java", "Spring", "MySQL");

        // Education 검증
        EducationResponse resultEducation = result.getEducations().get(0);
        assertThat(resultEducation.getSchool()).isEqualTo("테스트대학교");
        assertThat(resultEducation.getMajor()).isEqualTo("컴퓨터공학");
        assertThat(resultEducation.getEducationLevel()).isEqualTo(EducationLevel.COLLEGE_2_3);

        // Project 검증
        ProjectResponse resultProject = result.getProjects().get(0);
        assertThat(resultProject.getProjectName()).isEqualTo("테스트 프로젝트");
        assertThat(resultProject.getTechStack()).contains("Java", "Spring", "React");

        // Etc 검증
        EtcResponse resultEtc = result.getEtcs().get(0);
        assertThat(resultEtc.getTitle()).isEqualTo("자격증");
        assertThat(resultEtc.getType()).isEqualTo(EtcType.CERTIFICATE);

        // SelfIntroduction 검증
        SelfIntroductionResponse resultSelfIntro = result.getSelfIntroductions().get(0);
        assertThat(resultSelfIntro.getTitle()).isEqualTo("자기소개서");
        assertThat(resultSelfIntro.getContent()).isEqualTo("저는 열정적인 개발자입니다.");
    }
}