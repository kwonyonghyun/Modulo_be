// EducationControllerTest.java
package com.example.Modulo.controller;

import com.example.Modulo.dto.request.EducationCreateRequest;
import com.example.Modulo.dto.request.EducationUpdateRequest;
import com.example.Modulo.dto.response.EducationResponse;
import com.example.Modulo.global.enums.EducationLevel;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.service.EducationService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = EducationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class
                        })
        })
@MockBean(JpaMetamodelMappingContext.class)
class EducationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EducationService educationService;

    private EducationCreateRequest createRequest;
    private EducationUpdateRequest updateRequest;
    private EducationResponse educationResponse;

    @BeforeEach
    void setUp() throws Exception {
        createRequest = new EducationCreateRequest();
        setFieldValue(createRequest, "startDate", YearMonth.of(2020, 3));
        setFieldValue(createRequest, "endDate", YearMonth.of(2024, 2));
        setFieldValue(createRequest, "school", "테스트대학교");
        setFieldValue(createRequest, "major", "컴퓨터공학");
        setFieldValue(createRequest, "educationLevel", EducationLevel.COLLEGE_4);

        updateRequest = new EducationUpdateRequest();
        setFieldValue(updateRequest, "startDate", YearMonth.of(2020, 3));
        setFieldValue(updateRequest, "endDate", YearMonth.of(2024, 2));
        setFieldValue(updateRequest, "school", "테스트대학교");
        setFieldValue(updateRequest, "major", "컴퓨터공학");
        setFieldValue(updateRequest, "educationLevel", EducationLevel.COLLEGE_4);

        educationResponse = EducationResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2020, 3))
                .endDate(YearMonth.of(2024, 2))
                .school("테스트대학교")
                .major("컴퓨터공학")
                .educationLevel(EducationLevel.COLLEGE_4)
                .build();
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    @DisplayName("교육 정보 생성 성공")
    void createEducation_Success() throws Exception {
        // given
        given(educationService.createEducation(any(EducationCreateRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/education")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.EDUCATION_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("내 교육 정보 목록 조회 성공")
    void getMyEducations_Success() throws Exception {
        // given
        given(educationService.getMyEducations())
                .willReturn(List.of(educationResponse));

        // when & then
        mockMvc.perform(get("/api/education"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.EDUCATION_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].id").value(educationResponse.getId()));
    }

    @Test
    @WithMockUser
    @DisplayName("교육 정보 수정 성공")
    void updateEducation_Success() throws Exception {
        // given
        doNothing().when(educationService).updateEducation(any(Long.class), any(EducationUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/api/education/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.EDUCATION_UPDATE_SUCCESS.getCode()));
    }

    @Test
    @WithMockUser
    @DisplayName("교육 정보 삭제 성공")
    void deleteEducation_Success() throws Exception {
        // given
        doNothing().when(educationService).deleteEducation(any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/education/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.EDUCATION_DELETE_SUCCESS.getCode()));
    }
}