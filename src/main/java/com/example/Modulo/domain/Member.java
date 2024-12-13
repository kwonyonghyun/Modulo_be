package com.example.Modulo.domain;

import com.example.Modulo.global.common.BaseTimeEntity;
import com.example.Modulo.global.enums.OAuthProvider;
import com.example.Modulo.global.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;

    private String providerId;

    private Role role;

    @Builder
    public Member(String email, String name, OAuthProvider provider, String providerId) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.role = Role.ROLE_USER;
    }
}