package com.academia.bjj.auth.controller;

import com.academia.bjj.auth.dto.AuthResponse;
import com.academia.bjj.auth.dto.ForgotPasswordRequest;
import com.academia.bjj.auth.dto.LoginRequest;
import com.academia.bjj.auth.dto.RefreshRequest;
import com.academia.bjj.auth.dto.RegisterRequest;
import com.academia.bjj.auth.dto.ResetPasswordRequest;
import com.academia.bjj.auth.dto.UsuarioResponse;
import com.academia.bjj.auth.security.JwtAuthenticationFilter.AuthenticatedUser;
import com.academia.bjj.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de autenticacao (RF-096 a RF-101). Todos publicos exceto /me.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticacao", description = "Registro, login, refresh e recuperacao de senha")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuario (papel ALUNO) e retorna tokens")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica por e-mail e senha; retorna access + refresh token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Troca um refresh token valido por um novo par de tokens")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoga o refresh token informado")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Inicia recuperacao de senha; envia token por e-mail (sempre 204)")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefine a senha usando o token recebido por e-mail")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna os dados do usuario autenticado")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(authService.currentUser(user.id()));
    }
}
