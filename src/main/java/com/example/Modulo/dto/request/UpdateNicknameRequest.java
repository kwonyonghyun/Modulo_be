package com.example.Modulo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNicknameRequest {
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
}