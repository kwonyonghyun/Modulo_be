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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    private CacheManager cacheManager;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SavedModuleService savedModuleService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Cache cache;

    @Mock
    private ValueOperations<String, Object> valueOperations;

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

        // Cache related mocks
        lenient().when(cacheManager.getCache("basicInfo")).thenReturn(cache);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.getExpire(anyString())).thenReturn(1800L);
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
        verify(redisTemplate, times(3)).delete(anyString());
    }

    @Test
    @DisplayName("내 기본 정보 목록 조회 성공 - 캐시 TTL 연장")
    void getMyBasicInfos_Success_ExtendCacheTTL() {
        // given
        given(basicInfoRepository.findAllByMemberId(1L)).willReturn(List.of(basicInfo));
        given(redisTemplate.getExpire("basicInfo::member:1")).willReturn(1500L);

        // when
        List<BasicInfoResponse> responses = basicInfoService.getMyBasicInfos();

        // then
        assertThat(responses).hasSize(1);
        verify(redisTemplate).expire(anyString(), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("기본 정보 수정 성공 - 캐시 갱신")
    void updateBasicInfo_Success_CacheUpdate() {
        // given
        given(basicInfoRepository.findById(1L)).willReturn(Optional.of(basicInfo));

        // when
        basicInfoService.updateBasicInfo(1L, updateRequest, null);

        // then
        verify(redisTemplate, times(1)).delete(contains("savedModules::member:"));
        verify(redisTemplate, times(1)).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("기본 정보 삭제 성공 - 캐시 제거")
    void deleteBasicInfo_Success_CacheEviction() {
        // given
        given(basicInfoRepository.findById(1L)).willReturn(Optional.of(basicInfo));

        // when
        basicInfoService.deleteBasicInfo(1L);

        // then
        verify(s3Service).deleteFile("existing-image-url");
        verify(basicInfoRepository).delete(basicInfo);
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("기본 정보 단건 조회 성공 - 캐시 TTL 연장")
    void getBasicInfoById_Success_ExtendCacheTTL() {
        // given
        given(basicInfoRepository.findById(1L)).willReturn(Optional.of(basicInfo));
        given(redisTemplate.getExpire("basicInfo::basicInfo:1")).willReturn(1500L);

        // when
        BasicInfoResponse response = basicInfoService.getBasicInfoById(1L);

        // then
        assertThat(response.getId()).isEqualTo(basicInfo.getId());
        verify(redisTemplate).expire(anyString(), eq(3600L), eq(TimeUnit.SECONDS));
    }
}