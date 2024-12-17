package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidEmailException;
import com.example.Modulo.global.enums.OAuthProvider;
import com.example.Modulo.global.enums.Role;
import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MemberTest {
    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_NAME = "test";
    private static final OAuthProvider TEST_PROVIDER = OAuthProvider.GOOGLE;
    private static final String DEFAULT_NICKNAME = "Modulo";
    private static final Role DEFAULT_ROLE = Role.ROLE_USER;
    private static final String NEW_NICKNAME = "new nickname";
    private static final String INVALID_EMAIL = "invalid-email";

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email(TEST_EMAIL)
                .name(TEST_NAME)
                .provider(TEST_PROVIDER)
                .build();
    }

    @Test
    @DisplayName("Member 생성 테스트")
    void createMemberTest() {
        assertThat(member.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(member.getName()).isEqualTo(TEST_NAME);
        assertThat(member.getProvider()).isEqualTo(TEST_PROVIDER);
        assertThat(member.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(member.getNickname()).isEqualTo(DEFAULT_NICKNAME);
    }

    @Test
    @DisplayName("Member 닉네임 업데이트 테스트")
    void updateNicknameTest() {
        member.updateNickname(NEW_NICKNAME);

        assertThat(member.getNickname()).isEqualTo(NEW_NICKNAME);
    }

    @Test
    @DisplayName("잘못된 이메일 형식으로 Member 생성 실패")
    void createWithInvalidEmailTest() {
        BusinessException exception = assertThrows(InvalidEmailException.class, () -> Member.builder()
                .email(INVALID_EMAIL)
                .name(TEST_NAME)
                .provider(TEST_PROVIDER)
                .build());

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL);
    }

    @Test
    @DisplayName("null 이메일로 Member 생성 실패")
    void createWithNullEmailTest() {
        BusinessException exception = assertThrows(InvalidEmailException.class, () -> Member.builder()
                .email(null)
                .name(TEST_NAME)
                .provider(TEST_PROVIDER)
                .build());

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL);
    }
}