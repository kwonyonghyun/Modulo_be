package com.example.Modulo.service;

import com.example.Modulo.domain.Member;
import com.example.Modulo.domain.SelfIntroduction;
import com.example.Modulo.dto.request.SelfIntroductionCreateRequest;
import com.example.Modulo.dto.request.SelfIntroductionUpdateRequest;
import com.example.Modulo.dto.response.SelfIntroductionResponse;
import com.example.Modulo.exception.SelfIntroductionNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.MemberRepository;
import com.example.Modulo.repository.SelfIntroductionRepository;
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
class SelfIntroductionServiceTest {

    @InjectMocks
    private SelfIntroductionService selfIntroductionService;

    @Mock
    private SelfIntroductionRepository selfIntroductionRepository;

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
    private SelfIntroduction selfIntroduction;
    private SelfIntroductionCreateRequest createRequest;
    private SelfIntroductionUpdateRequest updateRequest;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();
        setId(member, 1L);

        selfIntroduction = SelfIntroduction.builder()
                .member(member)
                .title("자기소개서 제목")
                .content("자기소개서 내용")
                .build();
        setId(selfIntroduction, 1L);

        createRequest = new SelfIntroductionCreateRequest();
        setFieldValue(createRequest, "title", "자기소개서 제목");
        setFieldValue(createRequest, "content", "자기소개서 내용");

        updateRequest = new SelfIntroductionUpdateRequest();
        setFieldValue(updateRequest, "title", "수정된 제목");
        setFieldValue(updateRequest, "content", "수정된 내용");

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("1");

        lenient().when(cacheManager.getCache("selfIntroduction")).thenReturn(cache);
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
    @DisplayName("자기소개서 생성 성공 - 캐시 갱신")
    void createSelfIntroduction_Success_CacheUpdate() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(selfIntroductionRepository.save(any(SelfIntroduction.class))).willReturn(selfIntroduction);

        // when
        Long selfIntroductionId = selfIntroductionService.createSelfIntroduction(createRequest);

        // then
        assertThat(selfIntroductionId).isEqualTo(1L);
        verify(selfIntroductionRepository).save(any(SelfIntroduction.class));
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("내 자기소개서 목록 조회 성공 - 캐시 TTL 연장")
    void getMySelfIntroductions_Success_ExtendCacheTTL() {
        // given
        given(selfIntroductionRepository.findAllByMemberId(1L)).willReturn(List.of(selfIntroduction));
        given(redisTemplate.getExpire("selfIntroduction::member:1")).willReturn(1500L); // Below threshold

        // when
        List<SelfIntroductionResponse> responses = selfIntroductionService.getMySelfIntroductions();

        // then
        assertThat(responses).hasSize(1);
        verify(redisTemplate).expire(anyString(), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("자기소개서 단건 조회 성공 - 캐시 TTL 연장")
    void getIntroductionById_Success_ExtendCacheTTL() {
        // given
        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.of(selfIntroduction));
        given(redisTemplate.getExpire("selfIntroduction::selfIntroduction:1")).willReturn(1500L); // Below threshold

        // when
        SelfIntroductionResponse response = selfIntroductionService.getIntroductionById(1L);

        // then
        assertThat(response.getId()).isEqualTo(selfIntroduction.getId());
        verify(redisTemplate).expire(anyString(), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("자기소개서 수정 성공 - 캐시 갱신")
    void updateSelfIntroduction_Success_CacheUpdate() {
        // given
        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.of(selfIntroduction));

        // when
        selfIntroductionService.updateSelfIntroduction(1L, updateRequest);

        // then
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("자기소개서 삭제 성공 - 캐시 제거")
    void deleteSelfIntroduction_Success_CacheEviction() {
        // given
        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.of(selfIntroduction));

        // when
        selfIntroductionService.deleteSelfIntroduction(1L);

        // then
        verify(selfIntroductionRepository).delete(selfIntroduction);
        verify(redisTemplate).delete(contains("savedModules::member:"));
        verify(redisTemplate).delete(contains("resumes::member:"));
    }

    @Test
    @DisplayName("존재하지 않는 자기소개서 수정 시 예외 발생")
    void updateSelfIntroduction_NotFound() {
        // given
        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> selfIntroductionService.updateSelfIntroduction(1L, updateRequest))
                .isInstanceOf(SelfIntroductionNotFoundException.class);
    }

    @Test
    @DisplayName("권한 없는 자기소개서 수정 시 예외 발생")
    void updateSelfIntroduction_Unauthorized() throws Exception {
        // given
        Member otherMember = Member.builder()
                .email("other@test.com")
                .name("다른사용자")
                .build();
        setId(otherMember, 2L);

        SelfIntroduction otherSelfIntroduction = SelfIntroduction.builder()
                .member(otherMember)
                .title("다른 제목")
                .content("다른 내용")
                .build();
        setId(otherSelfIntroduction, 1L);

        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.of(otherSelfIntroduction));

        // when & then
        assertThatThrownBy(() -> selfIntroductionService.updateSelfIntroduction(1L, updateRequest))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
}