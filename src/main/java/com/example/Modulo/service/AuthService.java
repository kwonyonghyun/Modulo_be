package com.example.Modulo.service;

import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.response.TokenResponse;
import com.example.Modulo.exception.InvalidTokenException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.SocialLoginTokenNotFoundException;
import com.example.Modulo.exception.SocialLoginUserInfoNotFoundException;
import com.example.Modulo.global.enums.OAuthProvider;
import com.example.Modulo.global.jwt.JwtTokenProvider;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final String REFRESH_TOKEN_PREFIX = "refresh:";

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Value("${oauth2.google.client-id}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth2.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${oauth2.google.token-uri}")
    private String googleTokenUri;

    @Value("${oauth2.google.resource-uri}")
    private String googleResourceUri;

    @Transactional
    public TokenResponse googleLogin(String code) {
        String accessToken = getAccessToken(code);
        Map<String, Object> userInfo = getUserInfo(accessToken);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(email)
                        .name(name)
                        .provider(OAuthProvider.GOOGLE)
                        .build()));

        String jwtToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        redisService.setValues(REFRESH_TOKEN_PREFIX+member.getId(), refreshToken);

        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        Long memberId = Long.parseLong(jwtTokenProvider.getUserId(refreshToken));
        Member member = getMemberById(memberId);
        String storedRefreshToken = redisService.getValues(REFRESH_TOKEN_PREFIX+member.getId());
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new InvalidTokenException();
        }


        String newAccessToken = jwtTokenProvider.createAccessToken(memberId, member.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

        redisService.setValues(REFRESH_TOKEN_PREFIX+member.getId(), newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String accessToken) {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Long memberId = Long.parseLong(jwtTokenProvider.getUserId(accessToken));
        Member member = getMemberById(memberId);
        redisService.deleteValues(REFRESH_TOKEN_PREFIX+member.getId());
    }

    private String getAccessToken(String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(googleTokenUri, HttpMethod.POST, request, Map.class);

        if (response.getBody() == null) {
            throw new SocialLoginTokenNotFoundException();
        }

        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response = restTemplate.exchange(googleResourceUri, HttpMethod.GET, entity, Map.class);

        if (response.getBody() == null) {
            throw new SocialLoginUserInfoNotFoundException();
        }

        return response.getBody();
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

    }
}