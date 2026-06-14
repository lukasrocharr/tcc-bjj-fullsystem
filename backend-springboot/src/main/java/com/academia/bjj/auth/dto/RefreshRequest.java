package com.academia.bjj.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "O refresh token e obrigatorio")
        String refreshToken
) {
}
