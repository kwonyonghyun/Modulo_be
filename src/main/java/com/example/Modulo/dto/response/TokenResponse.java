package com.example.Modulo.dto.response;

import lombok.Data;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}