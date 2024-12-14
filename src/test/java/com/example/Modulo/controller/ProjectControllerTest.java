package com.example.Modulo.controller;

import com.example.Modulo.dto.request.ProjectCreateRequest;
import com.example.Modulo.dto.request.ProjectUpdateRequest;
import com.example.Modulo.dto.response.ProjectResponse;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.service.ProjectService;
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

@WebMvcTest(value = ProjectController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class
                        })
        })
@MockBean(JpaMetamodelMappingContext.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    private ProjectCreateRequest createRequest;
    private ProjectUpdateRequest updateRequest;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() throws Exception {
        createRequest = new ProjectCreateRequest();
        setFieldValue(createRequest, "startDate", YearMonth.of(2023, 1));
        setFieldValue(createRequest, "endDate", YearMonth.of(2023, 12));
        setFieldValue(createRequest, "projectName", "테스트 프로젝트");
        setFieldValue(createRequest, "shortDescription", "프로젝트 간단 설명");
        setFieldValue(createRequest, "techStack", Arrays.asList("Java", "Spring"));
        setFieldValue(createRequest, "teamComposition", "백엔드 2명, 프론트엔드 2명");
        setFieldValue(createRequest, "detailedDescription", "상세 설명");

        updateRequest = new ProjectUpdateRequest();
        setFieldValue(updateRequest, "startDate", YearMonth.of(2023, 1));
        setFieldValue(updateRequest, "endDate", YearMonth.of(2023, 12));
        setFieldValue(updateRequest, "projectName", "수정된 프로젝트");
        setFieldValue(updateRequest, "shortDescription", "수정된 설명");
        setFieldValue(updateRequest, "techStack", Arrays.asList("Java", "Spring", "React"));
        setFieldValue(updateRequest, "teamComposition", "백엔드 3명, 프론트엔드 3명");
        setFieldValue(updateRequest, "detailedDescription", "수정된 상세 설명");

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .projectName("테스트 프로젝트")
                .shortDescription("프로젝트 간단 설명")
                .techStack(Arrays.asList("Java", "Spring"))
                .teamComposition("백엔드 2명, 프론트엔드 2명")
                .detailedDescription("상세 설명")
                .build();
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 생성 성공")
    void createProject_Success() throws Exception {
        // given
        given(projectService.createProject(any(ProjectCreateRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/project")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.PROJECT_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("내 프로젝트 목록 조회 성공")
    void getMyProjects_Success() throws Exception {
        // given
        given(projectService.getMyProjects())
                .willReturn(List.of(projectResponse));

        // when & then
        mockMvc.perform(get("/api/project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.PROJECT_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].id").value(projectResponse.getId()));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 수정 성공")
    void updateProject_Success() throws Exception {
        // given
        doNothing().when(projectService).updateProject(any(Long.class), any(ProjectUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/api/project/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.PROJECT_UPDATE_SUCCESS.getCode()));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 삭제 성공")
    void deleteProject_Success() throws Exception {
        // given
        doNothing().when(projectService).deleteProject(any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/project/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.PROJECT_DELETE_SUCCESS.getCode()));
    }
} 