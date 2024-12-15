package com.example.Modulo.controller;

import com.example.Modulo.dto.response.*;
import com.example.Modulo.global.enums.CareerYear;
import com.example.Modulo.global.enums.EducationLevel;
import com.example.Modulo.global.enums.EtcType;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.service.SavedModuleService;
import com.example.Modulo.global.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SavedModuleController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class
                        })
        })
@MockBean(JpaMetamodelMappingContext.class)
class SavedModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SavedModuleService savedModuleService;

    private SavedModuleResponse savedModuleResponse;

    @BeforeEach
    void setUp() {
        BasicInfoResponse.LinkResponse linkResponse = BasicInfoResponse.LinkResponse.builder()
                .id(1L)
                .title("GitHub")
                .url("https://github.com/test")
                .build();

        BasicInfoResponse basicInfoResponse = BasicInfoResponse.builder()
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

        CareerResponse careerResponse = CareerResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2020, 3))
                .endDate(YearMonth.of(2024, 2))
                .companyName("테스트회사")
                .companyDescription("IT 기업")
                .position("백엔드 개발자")
                .techStack(Arrays.asList("Java", "Spring", "MySQL"))
                .achievements("주요 성과")
                .build();

        EducationResponse educationResponse = EducationResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2018, 3))
                .endDate(YearMonth.of(2022, 2))
                .school("테스트대학교")
                .major("컴퓨터공학")
                .educationLevel(EducationLevel.COLLEGE_2_3)
                .build();

        ProjectResponse projectResponse = ProjectResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .projectName("테스트 프로젝트")
                .shortDescription("프로젝트 설명")
                .techStack(Arrays.asList("Java", "Spring", "React"))
                .teamComposition("백엔드 2명, 프론트엔드 2명")
                .detailedDescription("상세 프로젝트 설명")
                .build();

        EtcResponse etcResponse = EtcResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .title("자격증")
                .description("정보처리기사")
                .type(EtcType.CERTIFICATE)
                .organization("한국산업인력공단")
                .score("합격")
                .build();

        SelfIntroductionResponse selfIntroductionResponse = SelfIntroductionResponse.builder()
                .title("자기소개서")
                .content("저는 열정적인 개발자입니다.")
                .build();

        savedModuleResponse = SavedModuleResponse.builder()
                .basicInfos(List.of(basicInfoResponse))
                .careers(List.of(careerResponse))
                .educations(List.of(educationResponse))
                .projects(List.of(projectResponse))
                .etcs(List.of(etcResponse))
                .selfIntroductions(List.of(selfIntroductionResponse))
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("저장된 모듈 조회 성공")
    void getMySavedModules_Success() throws Exception {
        // given
        given(savedModuleService.getMySavedModules())
                .willReturn(savedModuleResponse);

        // when & then
        mockMvc.perform(get("/api/saved-modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SAVED_MODULE_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.basicInfos[0].name").value("홍길동"))
                .andExpect(jsonPath("$.data.basicInfos[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.data.basicInfos[0].links[0].title").value("GitHub"))
                .andExpect(jsonPath("$.data.careers[0].companyName").value("테스트회사"))
                .andExpect(jsonPath("$.data.careers[0].position").value("백엔드 개발자"))
                .andExpect(jsonPath("$.data.educations[0].school").value("테스트대학교"))
                .andExpect(jsonPath("$.data.educations[0].major").value("컴퓨터공학"))
                .andExpect(jsonPath("$.data.projects[0].projectName").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.data.etcs[0].title").value("자격증"))
                .andExpect(jsonPath("$.data.selfIntroductions[0].title").value("자기소개서"));
    }
}