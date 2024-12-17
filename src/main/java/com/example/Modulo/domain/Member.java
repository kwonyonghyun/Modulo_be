package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidEmailException;
import com.example.Modulo.global.common.BaseTimeEntity;
import com.example.Modulo.global.enums.OAuthProvider;
import com.example.Modulo.global.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String nickname;

    @Builder
    public Member(String email, String name, OAuthProvider provider) {
        validateEmail(email);
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.role = Role.ROLE_USER;
        this.nickname = "Modulo";
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException(email);
        }
    }
}
