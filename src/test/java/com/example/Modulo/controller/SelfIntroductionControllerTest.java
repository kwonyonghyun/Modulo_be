package com.example.Modulo.controller;

import com.example.Modulo.dto.request.SelfIntroductionCreateRequest;
import com.example.Modulo.dto.request.SelfIntroductionUpdateRequest;
import com.example.Modulo.dto.response.SelfIntroductionResponse;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.service.SelfIntroductionService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SelfIntroductionController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class
                        })
        })
@MockBean(JpaMetamodelMappingContext.class)
class SelfIntroductionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SelfIntroductionService selfIntroductionService;

    private SelfIntroductionCreateRequest createRequest;
    private SelfIntroductionUpdateRequest updateRequest;
    private SelfIntroductionResponse selfIntroductionResponse;

    @BeforeEach
    void setUp() throws Exception {
        createRequest = new SelfIntroductionCreateRequest();
        setFieldValue(createRequest, "title", "자기소개서 제목");
        setFieldValue(createRequest, "content", "자기소개서 내용");

        updateRequest = new SelfIntroductionUpdateRequest();
        setFieldValue(updateRequest, "title", "수정된 제목");
        setFieldValue(updateRequest, "content", "수정된 내용");

        selfIntroductionResponse = SelfIntroductionResponse.builder()
                .title("자기소개서 제목")
                .content("자기소개서 내용")
                .build();
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    @DisplayName("자기소개서 생성 성공")
    void createSelfIntroduction_Success() throws Exception {
        // given
        given(selfIntroductionService.createSelfIntroduction(any(SelfIntroductionCreateRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/self-introduction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SELF_INTRODUCTION_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("내 자기소개서 목록 조회 성공")
    void getMySelfIntroductions_Success() throws Exception {
        // given
        given(selfIntroductionService.getMySelfIntroductions())
                .willReturn(List.of(selfIntroductionResponse));

        // when & then
        mockMvc.perform(get("/api/self-introduction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SELF_INTRODUCTION_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].title").value(selfIntroductionResponse.getTitle()));
    }

    @Test
    @WithMockUser
    @DisplayName("자기소개서 수정 성공")
    void updateSelfIntroduction_Success() throws Exception {
        // given
        doNothing().when(selfIntroductionService).updateSelfIntroduction(any(Long.class), any(SelfIntroductionUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/api/self-introduction/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SELF_INTRODUCTION_UPDATE_SUCCESS.getCode()));
    }

    @Test
    @WithMockUser
    @DisplayName("자기소개서 삭제 성공")
    void deleteSelfIntroduction_Success() throws Exception {
        // given
        doNothing().when(selfIntroductionService).deleteSelfIntroduction(any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/self-introduction/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SELF_INTRODUCTION_DELETE_SUCCESS.getCode()));
    }
}