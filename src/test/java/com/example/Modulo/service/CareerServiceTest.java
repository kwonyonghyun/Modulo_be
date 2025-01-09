package com.example.Modulo.service;

import com.example.Modulo.domain.Career;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.CareerCreateRequest;
import com.example.Modulo.dto.request.CareerUpdateRequest;
import com.example.Modulo.dto.response.CareerResponse;
import com.example.Modulo.exception.CareerNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.CareerRepository;
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
import java.util.Arrays;
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
class CareerServiceTest {

    @InjectMocks
    private CareerService careerService;

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SavedModuleService savedModuleService;

    @Mock
    private ResumeSectionHandler resumeSectionHandler;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Cache cache;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private Member member;
    private Career career;
    private CareerCreateRequest createRequest;
    private CareerUpdateRequest updateRequest;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();
        setId(member, 1L);

        career = Career.builder()
                .member(member)
                .startDate(YearMonth.of(2020, 3))
                .endDate(YearMonth.of(2024, 2))
                .companyName("테스트회사")
                .companyDescription("회사 설명")
                .position("백엔드 개발자")
                .techStack(Arrays.asList("Java", "Spring", "MySQL"))
                .achievements("주요 성과")
                .build();
        setId(career, 1L);

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

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("1");


        lenient().when(cacheManager.getCache("career")).thenReturn(cache);
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
    @DisplayName("경력 정보 생성 성공 - 캐시 갱신")
    void createCareer_Success_CacheUpdate() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(careerRepository.save(any(Career.class))).willReturn(career);

        // when
        Long careerId = careerService.createCareer(createRequest);

        // then
        assertThat(careerId).isEqualTo(1L);
        verify(careerRepository).save(any(Career.class));
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("내 경력 정보 목록 조회 성공 - 캐시 TTL 연장")
    void getMyCareers_Success_ExtendCacheTTL() {
        // given
        given(careerRepository.findAllByMemberId(1L)).willReturn(List.of(career));
        given(redisTemplate.getExpire("career::member:1")).willReturn(1500L);

        // when
        List<CareerResponse> responses = careerService.getMyCareers();

        // then
        assertThat(responses).hasSize(1);
        verify(redisTemplate).expire(anyString(), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("경력 정보 단건 조회 성공 - 캐시 TTL 연장")
    void getCareerById_Success_ExtendCacheTTL() {
        // given
        given(careerRepository.findById(1L)).willReturn(Optional.of(career));
        given(redisTemplate.getExpire("career::career:1")).willReturn(1500L);

        // when
        CareerResponse response = careerService.getCareerById(1L);

        // then
        assertThat(response.getId()).isEqualTo(career.getId());
        verify(redisTemplate).expire(anyString(), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("경력 정보 수정 성공 - 캐시 갱신")
    void updateCareer_Success_CacheUpdate() {
        // given
        given(careerRepository.findById(1L)).willReturn(Optional.of(career));

        // when
        careerService.updateCareer(1L, updateRequest);

        // then
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("경력 정보 삭제 성공 - 캐시 제거")
    void deleteCareer_Success_CacheEviction() {
        // given
        given(careerRepository.findById(1L)).willReturn(Optional.of(career));

        // when
        careerService.deleteCareer(1L);

        // then
        verify(careerRepository).delete(career);
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("존재하지 않는 경력 정보 수정 시 예외 발생")
    void updateCareer_NotFound() {
        // given
        given(careerRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> careerService.updateCareer(1L, updateRequest))
                .isInstanceOf(CareerNotFoundException.class);
    }

    @Test
    @DisplayName("권한 없는 경력 정보 수정 시 예외 발생")
    void updateCareer_Unauthorized() throws Exception {
        // given
        Member otherMember = Member.builder()
                .email("other@test.com")
                .name("다른사용자")
                .build();
        setId(otherMember, 2L);

        Career otherCareer = Career.builder()
                .member(otherMember)
                .startDate(YearMonth.of(2020, 3))
                .endDate(YearMonth.of(2024, 2))
                .companyName("다른회사")
                .companyDescription("다른 설명")
                .position("다른 직책")
                .techStack(Arrays.asList("Python", "Django"))
                .achievements("다른 성과")
                .build();
        setId(otherCareer, 1L);

        given(careerRepository.findById(1L)).willReturn(Optional.of(otherCareer));

        // when & then
        assertThatThrownBy(() -> careerService.updateCareer(1L, updateRequest))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
}