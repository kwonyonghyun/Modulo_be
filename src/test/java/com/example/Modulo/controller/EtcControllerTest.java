package com.example.Modulo.controller;

import com.example.Modulo.dto.request.EtcCreateRequest;
import com.example.Modulo.dto.request.EtcUpdateRequest;
import com.example.Modulo.dto.response.EtcResponse;
import com.example.Modulo.global.enums.EtcType;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.service.EtcService;
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

@WebMvcTest(value = EtcController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class
                        })
        })
@MockBean(JpaMetamodelMappingContext.class)
class EtcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EtcService etcService;

    private EtcCreateRequest createRequest;
    private EtcUpdateRequest updateRequest;
    private EtcResponse etcResponse;

    @BeforeEach
    void setUp() throws Exception {
        createRequest = new EtcCreateRequest();
        setFieldValue(createRequest, "startDate", YearMonth.of(2023, 1));
        setFieldValue(createRequest, "endDate", YearMonth.of(2023, 12));
        setFieldValue(createRequest, "title", "SQLD");
        setFieldValue(createRequest, "description", "SQL 개발자 자격증");
        setFieldValue(createRequest, "type", EtcType.CERTIFICATE);
        setFieldValue(createRequest, "organization", "한국데이터산업진흥원");
        setFieldValue(createRequest, "score", "최종합격");

        updateRequest = new EtcUpdateRequest();
        setFieldValue(updateRequest, "startDate", YearMonth.of(2023, 1));
        setFieldValue(updateRequest, "endDate", YearMonth.of(2023, 12));
        setFieldValue(updateRequest, "title", "SQLP");
        setFieldValue(updateRequest, "description", "SQL 전문가 자격증");
        setFieldValue(updateRequest, "type", EtcType.CERTIFICATE);
        setFieldValue(updateRequest, "organization", "한국데이터산업진흥원");
        setFieldValue(updateRequest, "score", "최종합격");

        etcResponse = EtcResponse.builder()
                .id(1L)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .title("SQLD")
                .description("SQL 개발자 자격증")
                .type(EtcType.CERTIFICATE)
                .organization("한국데이터산업진흥원")
                .score("최종합격")
                .build();
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @WithMockUser
    @DisplayName("기타사항 생성 성공")
    void createEtc_Success() throws Exception {
        // given
        given(etcService.createEtc(any(EtcCreateRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/etc")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.ETC_CREATE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("내 기타사항 목록 조회 성공")
    void getMyEtcs_Success() throws Exception {
        // given
        given(etcService.getMyEtcs())
                .willReturn(List.of(etcResponse));

        // when & then
        mockMvc.perform(get("/api/etc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.ETC_GET_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data[0].id").value(etcResponse.getId()));
    }

    @Test
    @WithMockUser
    @DisplayName("기타사항 수정 성공")
    void updateEtc_Success() throws Exception {
        // given
        doNothing().when(etcService).updateEtc(any(Long.class), any(EtcUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/api/etc/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.ETC_UPDATE_SUCCESS.getCode()));
    }

    @Test
    @WithMockUser
    @DisplayName("기타사항 삭제 성공")
    void deleteEtc_Success() throws Exception {
        // given
        doNothing().when(etcService).deleteEtc(any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/etc/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.ETC_DELETE_SUCCESS.getCode()));
    }
} 