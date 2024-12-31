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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SelfIntroductionServiceTest {

    @InjectMocks
    private SelfIntroductionService selfIntroductionService;

    @Mock
    private SelfIntroductionRepository selfIntroductionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

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

        Field memberIdField = member.getClass().getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(member, 1L);

        selfIntroduction = SelfIntroduction.builder()
                .member(member)
                .title("자기소개서 제목")
                .content("자기소개서 내용")
                .build();

        Field selfIntroductionIdField = selfIntroduction.getClass().getDeclaredField("id");
        selfIntroductionIdField.setAccessible(true);
        selfIntroductionIdField.set(selfIntroduction, 1L);

        createRequest = new SelfIntroductionCreateRequest();
        setFieldValue(createRequest, "title", "자기소개서 제목");
        setFieldValue(createRequest, "content", "자기소개서 내용");

        updateRequest = new SelfIntroductionUpdateRequest();
        setFieldValue(updateRequest, "title", "수정된 제목");
        setFieldValue(updateRequest, "content", "수정된 내용");

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("1");
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @DisplayName("자기소개서 생성 성공")
    void createSelfIntroduction_Success() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(selfIntroductionRepository.save(any(SelfIntroduction.class))).willReturn(selfIntroduction);

        // when
        Long selfIntroductionId = selfIntroductionService.createSelfIntroduction(createRequest);

        // then
        assertThat(selfIntroductionId).isEqualTo(1L);
        verify(selfIntroductionRepository).save(any(SelfIntroduction.class));
    }

    @Test
    @DisplayName("내 자기소개서 목록 조회 성공")
    void getMySelfIntroductions_Success() {
        // given
        given(selfIntroductionRepository.findAllByMemberId(1L)).willReturn(List.of(selfIntroduction));

        // when
        List<SelfIntroductionResponse> result = selfIntroductionService.getMySelfIntroductions();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(selfIntroduction.getTitle());
        assertThat(result.get(0).getContent()).isEqualTo(selfIntroduction.getContent());
    }

    @Test
    @DisplayName("내 자기소개서 단건 조회 성공")
    void getMySelfIntroductions_NotFound() {
        //given
        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.of(selfIntroduction));

        //when
        SelfIntroductionResponse result = selfIntroductionService.getIntroductionById(1L);

        //then
        assertThat(result.getContent()).isEqualTo(selfIntroduction.getContent());
        assertThat(result.getTitle()).isEqualTo(selfIntroduction.getTitle());
    }

    @Test
    @DisplayName("자기소개서 수정 성공")
    void updateSelfIntroduction_Success() {
        // given
        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.of(selfIntroduction));

        // when
        selfIntroductionService.updateSelfIntroduction(1L, updateRequest);

        // then
        assertThat(selfIntroduction.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(selfIntroduction.getContent()).isEqualTo(updateRequest.getContent());
    }

    @Test
    @DisplayName("자기소개서 삭제 성공")
    void deleteSelfIntroduction_Success() {
        // given
        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.of(selfIntroduction));

        // when
        selfIntroductionService.deleteSelfIntroduction(1L);

        // then
        verify(selfIntroductionRepository).delete(selfIntroduction);
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

        Field otherMemberIdField = otherMember.getClass().getDeclaredField("id");
        otherMemberIdField.setAccessible(true);
        otherMemberIdField.set(otherMember, 2L);

        SelfIntroduction otherSelfIntroduction = SelfIntroduction.builder()
                .member(otherMember)
                .title("다른 제목")
                .content("다른 내용")
                .build();

        Field otherSelfIntroductionIdField = otherSelfIntroduction.getClass().getDeclaredField("id");
        otherSelfIntroductionIdField.setAccessible(true);
        otherSelfIntroductionIdField.set(otherSelfIntroduction, 1L);

        given(selfIntroductionRepository.findById(1L)).willReturn(Optional.of(otherSelfIntroduction));

        // when & then
        assertThatThrownBy(() -> selfIntroductionService.updateSelfIntroduction(1L, updateRequest))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
} 