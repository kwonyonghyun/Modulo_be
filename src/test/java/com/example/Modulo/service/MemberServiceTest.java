package com.example.Modulo.service;

import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.UpdateNicknameRequest;
import com.example.Modulo.dto.response.MemberResponse;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private UpdateNicknameRequest updateRequest;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();

        Field memberIdField = member.getClass().getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(member, 1L);

        updateRequest = new UpdateNicknameRequest();
        setFieldValue(updateRequest, "nickname", "새로운닉네임");
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    void getMember_Success() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        // when
        MemberResponse response = memberService.getMember(1L);

        // then
        assertThat(response.getId()).isEqualTo(member.getId());
        assertThat(response.getName()).isEqualTo(member.getName());
        assertThat(response.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 회원 조회 시 예외 발생")
    void getMember_NotFound() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMember(1L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("닉네임 변경 성공")
    void updateNickname_Success() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        // when
        MemberResponse response = memberService.updateNickname(1L, updateRequest);

        // then
        verify(memberRepository).findById(1L);
        assertThat(member.getNickname()).isEqualTo(updateRequest.getNickname());
        assertThat(response.getNickname()).isEqualTo(updateRequest.getNickname());
    }

    @Test
    @DisplayName("존재하지 않는 회원의 닉네임 변경 시 예외 발생")
    void updateNickname_NotFound() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.updateNickname(1L, updateRequest))
                .isInstanceOf(MemberNotFoundException.class);
    }
}