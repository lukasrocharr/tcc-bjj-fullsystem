package com.academia.bjj.auth.service;

import com.academia.bjj.auth.dto.AuthResponse;
import com.academia.bjj.auth.dto.ForgotPasswordRequest;
import com.academia.bjj.auth.dto.LoginRequest;
import com.academia.bjj.auth.dto.RefreshRequest;
import com.academia.bjj.auth.dto.RegisterRequest;
import com.academia.bjj.auth.dto.ResetPasswordRequest;
import com.academia.bjj.auth.dto.UsuarioResponse;

/**
 * Casos de uso de autenticacao (RF-096 a RF-101).
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void logout(RefreshRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    UsuarioResponse currentUser(Long usuarioId);
}
