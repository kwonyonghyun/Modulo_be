package com.example.Modulo.service;

import com.example.Modulo.domain.Education;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.EducationCreateRequest;
import com.example.Modulo.dto.request.EducationUpdateRequest;
import com.example.Modulo.dto.response.EducationResponse;
import com.example.Modulo.exception.EducationNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.global.enums.EducationLevel;
import com.example.Modulo.repository.EducationRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @InjectMocks
    private EducationService educationService;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private MemberRepository memberRepository;

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
    private Education education;
    private EducationCreateRequest createRequest;
    private EducationUpdateRequest updateRequest;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();
        setId(member, 1L);

        education = Education.builder()
                .member(member)
                .startDate(YearMonth.of(2020, 3))
                .endDate(YearMonth.of(2024, 2))
                .school("테스트대학교")
                .major("컴퓨터공학")
                .educationLevel(EducationLevel.COLLEGE_4)
                .build();
        setId(education, 1L);

        createRequest = new EducationCreateRequest();
        setFieldValue(createRequest, "startDate", YearMonth.of(2020, 3));
        setFieldValue(createRequest, "endDate", YearMonth.of(2024, 2));
        setFieldValue(createRequest, "school", "테스트대학교");
        setFieldValue(createRequest, "major", "컴퓨터공학");
        setFieldValue(createRequest, "educationLevel", EducationLevel.COLLEGE_4);

        updateRequest = new EducationUpdateRequest();
        setFieldValue(updateRequest, "startDate", YearMonth.of(2020, 3));
        setFieldValue(updateRequest, "endDate", YearMonth.of(2024, 2));
        setFieldValue(updateRequest, "school", "수정된대학교");
        setFieldValue(updateRequest, "major", "수정된학과");
        setFieldValue(updateRequest, "educationLevel", EducationLevel.COLLEGE_4);

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("1");

        lenient().when(cacheManager.getCache("education")).thenReturn(cache);
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
    @DisplayName("교육 정보 생성 성공 - 캐시 갱신")
    void createEducation_Success_CacheUpdate() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(educationRepository.save(any(Education.class))).willReturn(education);

        // when
        Long educationId = educationService.createEducation(createRequest);

        // then
        assertThat(educationId).isEqualTo(1L);
        verify(educationRepository).save(any(Education.class));
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("내 교육 정보 목록 조회 성공 - 캐시 TTL 연장")
    void getMyEducations_Success_ExtendCacheTTL() {
        // given
        given(educationRepository.findAllByMemberId(1L)).willReturn(List.of(education));
        given(redisTemplate.getExpire("education::member:1")).willReturn(1500L);

        // when
        List<EducationResponse> responses = educationService.getMyEducations();

        // then
        assertThat(responses).hasSize(1);
        verify(redisTemplate).expire(anyString(), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("교육 정보 단건 조회 성공 - 캐시 TTL 연장")
    void getEducationById_Success_ExtendCacheTTL() {
        // given
        given(educationRepository.findById(1L)).willReturn(Optional.of(education));
        given(redisTemplate.getExpire("education::education:1")).willReturn(1500L);

        // when
        EducationResponse response = educationService.getEducationById(1L);

        // then
        assertThat(response.getId()).isEqualTo(education.getId());
        verify(redisTemplate).expire(anyString(), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("교육 정보 수정 성공 - 캐시 갱신")
    void updateEducation_Success_CacheUpdate() {
        // given
        given(educationRepository.findById(1L)).willReturn(Optional.of(education));

        // when
        educationService.updateEducation(1L, updateRequest);

        // then
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("교육 정보 삭제 성공 - 캐시 제거")
    void deleteEducation_Success_CacheEviction() {
        // given
        given(educationRepository.findById(1L)).willReturn(Optional.of(education));

        // when
        educationService.deleteEducation(1L);

        // then
        verify(educationRepository).delete(education);
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("존재하지 않는 교육 정보 수정 시 예외 발생")
    void updateEducation_NotFound() {
        // given
        given(educationRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> educationService.updateEducation(1L, updateRequest))
                .isInstanceOf(EducationNotFoundException.class);
    }

    @Test
    @DisplayName("권한 없는 교육 정보 수정 시 예외 발생")
    void updateEducation_Unauthorized() throws Exception {
        // given
        Member otherMember = Member.builder()
                .email("other@test.com")
                .name("다른사용자")
                .build();
        setId(otherMember, 2L);

        Education otherEducation = Education.builder()
                .member(otherMember)
                .startDate(YearMonth.of(2020, 3))
                .endDate(YearMonth.of(2024, 2))
                .school("다른대학교")
                .major("다른학과")
                .educationLevel(EducationLevel.COLLEGE_4)
                .build();
        setId(otherEducation, 1L);

        given(educationRepository.findById(1L)).willReturn(Optional.of(otherEducation));

        // when & then
        assertThatThrownBy(() -> educationService.updateEducation(1L, updateRequest))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
}