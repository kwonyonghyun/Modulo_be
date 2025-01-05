package com.example.Modulo.controller;

import com.example.Modulo.dto.response.TokenResponse;
import com.example.Modulo.global.annotation.TrackUserActivity;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@TrackUserActivity
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/google")
    public ResponseEntity<ResultResponse> googleLogin(@RequestParam("accessToken") String accessToken) {
        TokenResponse tokenResponse = authService.googleLogin(accessToken);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SOCIAL_LOGIN_SUCCESS, tokenResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResultResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        TokenResponse tokenResponse = authService.refresh(refreshToken);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.TOKEN_REISSUED_SUCCESS, tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResultResponse> logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.LOGOUT_SUCCESS));
    }

    @GetMapping("/test")
    public String test() {
        return "돼땅";
    }
}