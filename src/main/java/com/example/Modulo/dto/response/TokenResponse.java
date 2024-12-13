package com.example.Modulo.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}