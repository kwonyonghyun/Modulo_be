package com.example.Modulo.controller;

import com.example.Modulo.dto.request.ResumeCreateRequest;
import com.example.Modulo.dto.request.ResumeUpdateRequest;
import com.example.Modulo.dto.request.ResumeSectionRequest;
import com.example.Modulo.dto.request.SectionContentRequest;
import com.example.Modulo.dto.response.ResumeDetailResponse;
import com.example.Modulo.dto.response.ResumeResponse;
import com.example.Modulo.global.config.SecurityConfig;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.service.ResumeService;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(value = ResumeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class
                        })
        })
@MockBean(JpaMetamodelMappingContext.class)
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResumeService resumeService;

    private ResumeCreateRequest createRequest;
    private ResumeUpdateRequest updateRequest;
    private ResumeResponse resumeResponse;
    private ResumeDetailResponse resumeDetailResponse;

    @BeforeEach
    void setUp() throws Exception {
        List<ResumeSectionRequest> sections = new ArrayList<>();
        List<SectionContentRequest> contents = new ArrayList<>();
        contents.add(createSectionContentRequest(1, 10, 1L));
        sections.add(createResumeSectionRequest(1, 20, SectionType.BASIC_INFO, contents));

        createRequest = new ResumeCreateRequest();
        setFieldValue(createRequest, "title", "테스트 이력서");
        setFieldValue(createRequest, "sections", sections);

        updateRequest = new ResumeUpdateRequest();
        setFieldValue(updateRequest, "title", "수정된 이력서");
        setFieldValue(updateRequest, "sections", sections);

        resumeResponse = ResumeResponse.builder()
                .id(1L)
                .title("테스트 이력서")
                .sections(new ArrayList<>())
                .build();

        resumeDetailResponse = ResumeDetailResponse.builder()
                .id(1L)
                .title("테스트 이력서")
                .sections(new ArrayList<>())
                .build();
    }

    private ResumeSectionRequest createResumeSectionRequest(int orderIndex, int topMargin,
                                                            SectionType sectionType, List<SectionContentRequest> contents) throws Exception {
        ResumeSectionRequest request = new ResumeSectionRequest();
        setFieldValue(request, "orderIndex", orderIndex);
        setFieldValue(request, "topMargin", topMargin);
        setFieldValue(request, "sectionType", sectionType);
        setFieldValue(request, "contents", contents);
        return request;
    }

    private SectionContentRequest createSectionContentRequest(int orderIndex, int topMargin,
                                                              Long contentId) throws Exception {
        SectionContentRequest request = new SectionContentRequest();
        setFieldValue(request, "orderIndex", orderIndex);
        setFieldValue(request, "topMargin", topMargin);
        setFieldValue(request, "contentId", contentId);
        return request;
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    @DisplayName("이력서 생성 성공")
    void createResume_Success() throws Exception {
        // given
        given(resumeService.createResume(any(ResumeCreateRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/resume")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RESUME_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("내 이력서 목록 조회 성공")
    void getMyResumes_Success() throws Exception {
        // given
        given(resumeService.getMyResumes())
                .willReturn(List.of(resumeResponse));

        // when & then
        mockMvc.perform(get("/api/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RESUME_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].id").value(resumeResponse.getId()));
    }

    @Test
    @WithMockUser
    @DisplayName("이력서 수정 성공")
    void updateResume_Success() throws Exception {
        // given
        doNothing().when(resumeService).updateResume(any(Long.class), any(ResumeUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/api/resume/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RESUME_UPDATE_SUCCESS.getCode()));
    }

    @Test
    @WithMockUser
    @DisplayName("이력서 삭제 성공")
    void deleteResume_Success() throws Exception {
        // given
        doNothing().when(resumeService).deleteResume(any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/resume/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RESUME_DELETE_SUCCESS.getCode()));
    }

    @Test
    @WithMockUser
    @DisplayName("이력서 상세 조회 성공")
    void getResumeDetail_Success() throws Exception {
        // given
        given(resumeService.getResumeDetailById(1L))
                .willReturn(resumeDetailResponse);

        // when & then
        mockMvc.perform(get("/api/resume/detail/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RESUME_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(resumeDetailResponse.getId()))
                .andExpect(jsonPath("$.data.title").value(resumeDetailResponse.getTitle()));
    }

    @Test
    @WithMockUser
    @DisplayName("단일 이력서 조회 성공")
    void getResume_Success() throws Exception {
        // given
        given(resumeService.getResumeById(1L))
                .willReturn(resumeResponse);

        // when & then
        mockMvc.perform(get("/api/resume/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.RESUME_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(resumeResponse.getId()))
                .andExpect(jsonPath("$.data.title").value(resumeResponse.getTitle()));
    }
}