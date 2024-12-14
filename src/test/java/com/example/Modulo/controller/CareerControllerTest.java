package com.example.Modulo.controller;

import com.example.Modulo.dto.request.CareerCreateRequest;
import com.example.Modulo.dto.request.CareerUpdateRequest;
import com.example.Modulo.dto.response.CareerResponse;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.service.CareerService;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CareerController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class
                        })
        })
@MockBean(JpaMetamodelMappingContext.class)
class CareerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CareerService careerService;

    private CareerCreateRequest createRequest;
    private CareerUpdateRequest updateRequest;
    private CareerResponse careerResponse;

    @BeforeEach
    void setUp() throws Exception {
        createRequest = new CareerCreateRequest();
        setFieldValue(createRequest, "startDate", YearMonth.of(2020, 3));
        setFieldValue(createRequest, "endDate", YearMonth.of(2024, 2));
        setFieldValue(createRequest, "companyName", "테스트회사");
        setFieldValue(createRequest, "companyDescription", "회사 설명");
        setFieldValue(createRequest, "position", "백엔드 개발자");
        setFieldValue(createRequest, "techStack", Arrays.asList("Java", "Spring", "MySQL"));
        setFieldValue(createRequest, "achievements", "주요 성과");

        updateRequest = new CareerUpdateRequest();
        setFieldValue(updateRequest, "startDate", YearMonth.of(2020, 3));
        setFieldValue(updateRequest, "endDate", YearMonth.of(2024, 2));
        setFieldValue(updateRequest, "companyName", "수정된회사");
        setFieldValue(updateRequest, "companyDescription", "수정된 설명");
        setFieldValue(updateRequest, "position", "시니어 개발자");
        setFieldValue(updateRequest, "techStack", Arrays.asList("Java", "Spring", "MySQL"));
        setFieldValue(updateRequest, "achievements", "수정된 성과");

        careerResponse = CareerResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2020, 3))
                .endDate(YearMonth.of(2024, 2))
                .companyName("테스트회사")
                .companyDescription("회사 설명")
                .position("백엔드 개발자")
                .techStack(Arrays.asList("Java", "Spring", "MySQL"))
                .achievements("주요 성과")
                .build();
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    @DisplayName("경력 정보 생성 성공")
    void createCareer_Success() throws Exception {
        // given
        given(careerService.createCareer(any(CareerCreateRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/career")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.CAREER_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("내 경력 정보 목록 조회 성공")
    void getMyCareers_Success() throws Exception {
        // given
        given(careerService.getMyCareers())
                .willReturn(List.of(careerResponse));

        // when & then
        mockMvc.perform(get("/api/career"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.CAREER_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].id").value(careerResponse.getId()));
    }

    @Test
    @WithMockUser
    @DisplayName("경력 정보 수정 성공")
    void updateCareer_Success() throws Exception {
        // given
        doNothing().when(careerService).updateCareer(any(Long.class), any(CareerUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/api/career/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.CAREER_UPDATE_SUCCESS.getCode()));
    }

    @Test
    @WithMockUser
    @DisplayName("경력 정보 삭제 성공")
    void deleteCareer_Success() throws Exception {
        // given
        doNothing().when(careerService).deleteCareer(any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/career/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.CAREER_DELETE_SUCCESS.getCode()));
    }
}