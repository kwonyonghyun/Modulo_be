package com.example.Modulo.controller;

import com.example.Modulo.dto.request.BasicInfoCreateRequest;
import com.example.Modulo.dto.request.BasicInfoUpdateRequest;
import com.example.Modulo.dto.response.BasicInfoResponse;
import com.example.Modulo.global.config.SecurityConfig;
import com.example.Modulo.global.enums.CareerYear;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.service.BasicInfoService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BasicInfoController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class})
        })
@MockBean(JpaMetamodelMappingContext.class)
class BasicInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BasicInfoService basicInfoService;

    private BasicInfoCreateRequest createRequest;
    private BasicInfoUpdateRequest updateRequest;
    private BasicInfoResponse basicInfoResponse;
    private MockMultipartFile profileImage;

    @BeforeEach
    void setUp() throws Exception {
        createRequest = new BasicInfoCreateRequest();
        setFieldValue(createRequest, "name", "홍길동");
        setFieldValue(createRequest, "email", "test@test.com");
        setFieldValue(createRequest, "careerYear", CareerYear.YEAR_3);
        setFieldValue(createRequest, "birthYear", 1990);
        setFieldValue(createRequest, "jobPosition", "Backend Developer");
        setFieldValue(createRequest, "shortBio", "안녕하세요");
        setFieldValue(createRequest, "techStack", Arrays.asList("Java", "Spring"));

        updateRequest = new BasicInfoUpdateRequest();
        setFieldValue(updateRequest, "name", "홍길동(수정)");
        setFieldValue(updateRequest, "email", "update@test.com");
        setFieldValue(updateRequest, "careerYear", CareerYear.YEAR_5);
        setFieldValue(updateRequest, "birthYear", 1991);
        setFieldValue(updateRequest, "jobPosition", "Senior Developer");
        setFieldValue(updateRequest, "shortBio", "안녕하세요(수정)");
        setFieldValue(updateRequest, "techStack", Arrays.asList("Java", "Spring", "AWS"));

        basicInfoResponse = BasicInfoResponse.builder()
                .id(1L)
                .profileImageUrl("http://example.com/image.jpg")
                .name("홍길동")
                .email("test@test.com")
                .careerYear(CareerYear.YEAR_3)
                .birthYear(1990)
                .jobPosition("Backend Developer")
                .shortBio("안녕하세요")
                .techStack(Arrays.asList("Java", "Spring"))
                .build();

        profileImage = new MockMultipartFile(
                "profileImage",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    @DisplayName("기본 정보 생성 성공")
    void createBasicInfo_Success() throws Exception {
        // given
        given(basicInfoService.createBasicInfo(any(BasicInfoCreateRequest.class), any()))
                .willReturn(1L);

        MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(createRequest).getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/basic-info")
                        .file(profileImage)
                        .file(requestJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.BASIC_INFO_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1));
    }


    @Test
    @WithMockUser
    @DisplayName("내 기본 정보 목록 조회 성공")
    void getMyBasicInfos_Success() throws Exception {
        // given
        given(basicInfoService.getMyBasicInfos())
                .willReturn(List.of(basicInfoResponse));

        // when & then
        mockMvc.perform(get("/api/basic-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.BASIC_INFO_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].id").value(basicInfoResponse.getId()));
    }

    @Test
    @WithMockUser
    @DisplayName("기본 정보 수정 성공")
    void updateBasicInfo_Success() throws Exception {
        // given
        doNothing().when(basicInfoService).updateBasicInfo(any(Long.class), any(BasicInfoUpdateRequest.class), any());

        MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(updateRequest).getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/basic-info/1")
                        .file(profileImage)
                        .file(requestJson)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.BASIC_INFO_UPDATE_SUCCESS.getCode()));
    }

    @Test
    @WithMockUser
    @DisplayName("기본 정보 삭제 성공")
    void deleteBasicInfo_Success() throws Exception {
        // given
        doNothing().when(basicInfoService).deleteBasicInfo(any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/basic-info/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.BASIC_INFO_DELETE_SUCCESS.getCode()));
    }
}