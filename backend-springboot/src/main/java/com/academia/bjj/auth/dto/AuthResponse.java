package com.academia.bjj.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UsuarioResponse usuario
) {
    public static AuthResponse of(String accessToken, String refreshToken,
                                  long expiresInSeconds, UsuarioResponse usuario) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds, usuario);
    }
}
