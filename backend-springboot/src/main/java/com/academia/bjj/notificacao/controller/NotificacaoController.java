package com.academia.bjj.notificacao.controller;

import com.academia.bjj.auth.security.JwtAuthenticationFilter.AuthenticatedUser;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.notificacao.InAppNotificacaoService;
import com.academia.bjj.notificacao.dto.ComunicadoRequest;
import com.academia.bjj.notificacao.dto.NotificacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Notificacoes in-app do usuario (RF-103) e comunicados em massa (RF-104).
 */
@RestController
@RequestMapping("/api/v1/notificacoes")
@Tag(name = "Notificacoes", description = "Notificacoes in-app e comunicados em massa")
public class NotificacaoController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";

    private final InAppNotificacaoService service;

    public NotificacaoController(InAppNotificacaoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista as notificacoes do usuario autenticado")
    public PageResponse<NotificacaoResponse> listar(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.listar(user.id(), pageable);
    }

    @GetMapping("/nao-lidas/contagem")
    public Map<String, Long> naoLidas(@AuthenticationPrincipal AuthenticatedUser user) {
        return Map.of("naoLidas", service.contarNaoLidas(user.id()));
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarLida(@AuthenticationPrincipal AuthenticatedUser user,
                                           @PathVariable Long id) {
        service.marcarLida(user.id(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/lidas")
    @Operation(summary = "Marca todas as notificacoes como lidas")
    public Map<String, Integer> marcarTodasLidas(@AuthenticationPrincipal AuthenticatedUser user) {
        return Map.of("atualizadas", service.marcarTodasLidas(user.id()));
    }

    @PostMapping("/comunicados")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Envia comunicado em massa (todos ou por papel)")
    public ResponseEntity<Map<String, Integer>> comunicado(@Valid @RequestBody ComunicadoRequest req) {
        int destinatarios = service.enviarComunicado(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("destinatarios", destinatarios));
    }
}
