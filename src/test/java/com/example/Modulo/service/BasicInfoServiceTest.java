package com.example.Modulo.service;

import com.example.Modulo.domain.BasicInfo;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.BasicInfoCreateRequest;
import com.example.Modulo.dto.request.BasicInfoUpdateRequest;
import com.example.Modulo.dto.response.BasicInfoResponse;
import com.example.Modulo.exception.BasicInfoNotFoundException;
import com.example.Modulo.exception.BasicInfoUnauthorizedException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.global.enums.CareerYear;
import com.example.Modulo.global.service.S3Service;
import com.example.Modulo.repository.BasicInfoRepository;
import com.example.Modulo.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicInfoServiceTest {

    @InjectMocks
    private BasicInfoService basicInfoService;

    @Mock
    private BasicInfoRepository basicInfoRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Member member;
    private BasicInfo basicInfo;
    private BasicInfoCreateRequest createRequest;
    private BasicInfoUpdateRequest updateRequest;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();
        setId(member, 1L);

        basicInfo = BasicInfo.builder()
                .member(member)
                .name("테스터")
                .email("test@test.com")
                .careerYear(CareerYear.YEAR_2)
                .birthYear(1990)
                .jobPosition("Backend Developer")
                .shortBio("테스트 소개")
                .profileImageUrl("existing-image-url")
                .build();
        setId(basicInfo, 1L);

        createRequest = new BasicInfoCreateRequest();
        setFieldValue(createRequest, "name", "테스터");
        setFieldValue(createRequest, "email", "test@test.com");
        setFieldValue(createRequest, "careerYear", CareerYear.YEAR_2);
        setFieldValue(createRequest, "birthYear", 1990);
        setFieldValue(createRequest, "jobPosition", "Backend Developer");
        setFieldValue(createRequest, "shortBio", "테스트 소개");
        setFieldValue(createRequest, "techStack", Arrays.asList("Java", "Spring"));

        updateRequest = new BasicInfoUpdateRequest();
        setFieldValue(updateRequest, "name", "수정된 테스터");
        setFieldValue(updateRequest, "email", "updated@test.com");
        setFieldValue(updateRequest, "careerYear", CareerYear.YEAR_3);
        setFieldValue(updateRequest, "birthYear", 1990);
        setFieldValue(updateRequest, "jobPosition", "Senior Backend Developer");
        setFieldValue(updateRequest, "shortBio", "수정된 소개");
        setFieldValue(updateRequest, "techStack", Arrays.asList("Java", "Spring", "JPA"));

        mockFile = new MockMultipartFile(
                "profileImage",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("1");
    }

    private void setId(Object object, Long id) throws Exception {
        Field idField = object.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(object, id);
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @DisplayName("기본 정보 생성 성공 - 프로필 이미지 없음")
    void createBasicInfo_Success_NoProfileImage() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(basicInfoRepository.save(any(BasicInfo.class))).willReturn(basicInfo);

        // when
        Long basicInfoId = basicInfoService.createBasicInfo(createRequest, null);

        // then
        assertThat(basicInfoId).isEqualTo(1L);
        verify(basicInfoRepository).save(any(BasicInfo.class));
        verify(s3Service, never()).uploadFile(anyString(), any());
    }

    @Test
    @DisplayName("기본 정보 생성 성공 - 프로필 이미지 있음")
    void createBasicInfo_Success_WithProfileImage() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(s3Service.uploadFile(anyString(), any())).willReturn("test-url");
        given(basicInfoRepository.save(any(BasicInfo.class))).willReturn(basicInfo);

        // when
        Long basicInfoId = basicInfoService.createBasicInfo(createRequest, mockFile);

        // then
        assertThat(basicInfoId).isEqualTo(1L);
        verify(basicInfoRepository).save(any(BasicInfo.class));
        verify(s3Service).uploadFile(anyString(), any());
    }

    @Test
    @DisplayName("기본 정보 생성 실패 - 회원 없음")
    void createBasicInfo_MemberNotFound() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> basicInfoService.createBasicInfo(createRequest, mockFile))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("기본 정보 수정 성공 - 이미지 업데이트")
    void updateBasicInfo_Success_UpdateImage() {
        // given
        given(basicInfoRepository.findById(1L)).willReturn(Optional.of(basicInfo));
        given(s3Service.uploadFile(anyString(), any())).willReturn("updated-test-url");

        // when
        basicInfoService.updateBasicInfo(1L, updateRequest, mockFile);

        // then
        assertThat(basicInfo.getName()).isEqualTo(updateRequest.getName());
        assertThat(basicInfo.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(basicInfo.getCareerYear()).isEqualTo(updateRequest.getCareerYear());
        assertThat(basicInfo.getProfileImageUrl()).isEqualTo("updated-test-url");
        verify(s3Service).deleteFile("existing-image-url");
        verify(s3Service).uploadFile(anyString(), any());
    }

    @Test
    @DisplayName("기본 정보 단건 조회 성공")
    void getBasicInfoById_Success() {
        // given
        given(basicInfoRepository.findById(1L)).willReturn(Optional.of(basicInfo));

        // when
        BasicInfoResponse response = basicInfoService.getBasicInfoById(1L);

        // then
        assertThat(response.getId()).isEqualTo(basicInfo.getId());
        assertThat(response.getName()).isEqualTo(basicInfo.getName());
        assertThat(response.getEmail()).isEqualTo(basicInfo.getEmail());
    }

    @Test
    @DisplayName("기본 정보 단건 조회 실패 - 존재하지 않는 정보")
    void getBasicInfoById_NotFound() {
        // given
        given(basicInfoRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> basicInfoService.getBasicInfoById(1L))
                .isInstanceOf(BasicInfoNotFoundException.class);
    }

    @Test
    @DisplayName("내 기본 정보 목록 조회 성공")
    void getMyBasicInfos_Success() {
        // given
        given(basicInfoRepository.findAllByMemberId(1L)).willReturn(List.of(basicInfo));

        // when
        List<BasicInfoResponse> responses = basicInfoService.getMyBasicInfos();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo(basicInfo.getName());
        assertThat(responses.get(0).getEmail()).isEqualTo(basicInfo.getEmail());
    }

    @Test
    @DisplayName("기본 정보 삭제 성공")
    void deleteBasicInfo_Success() {
        // given
        given(basicInfoRepository.findById(1L)).willReturn(Optional.of(basicInfo));

        // when
        basicInfoService.deleteBasicInfo(1L);

        // then
        verify(s3Service).deleteFile("existing-image-url");
        verify(basicInfoRepository).delete(basicInfo);
    }

    @Test
    @DisplayName("기본 정보 삭제 실패 - 권한 없음")
    void deleteBasicInfo_Unauthorized() throws Exception {
        // given
        Member otherMember = Member.builder()
                .email("other@test.com")
                .name("다른사용자")
                .build();
        setId(otherMember, 2L);

        BasicInfo otherBasicInfo = BasicInfo.builder()
                .member(otherMember)
                .name("다른사용자")
                .email("other@test.com")
                .profileImageUrl("other-image-url")
                .careerYear(CareerYear.YEAR_1)
                .build();
        setId(otherBasicInfo, 1L);

        given(basicInfoRepository.findById(1L)).willReturn(Optional.of(otherBasicInfo));

        // when & then
        assertThatThrownBy(() -> basicInfoService.deleteBasicInfo(1L))
                .isInstanceOf(BasicInfoUnauthorizedException.class);
    }

    @Test
    @DisplayName("기본 정보 수정 성공 - 이미지 없이 정보만 수정")
    void updateBasicInfo_Success_WithoutImage() {
        // given
        given(basicInfoRepository.findById(1L)).willReturn(Optional.of(basicInfo));

        // when
        basicInfoService.updateBasicInfo(1L, updateRequest, null);

        // then
        assertThat(basicInfo.getName()).isEqualTo(updateRequest.getName());
        assertThat(basicInfo.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(basicInfo.getCareerYear()).isEqualTo(updateRequest.getCareerYear());
        verify(s3Service, never()).deleteFile(anyString());
        verify(s3Service, never()).uploadFile(anyString(), any());
    }
}