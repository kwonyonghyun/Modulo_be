package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Member;
import com.example.Modulo.global.enums.OAuthProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private OAuthProvider provider;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .provider(member.getProvider())
                .build();
    }
}