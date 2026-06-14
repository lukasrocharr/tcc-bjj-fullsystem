package com.academia.bjj.auditoria.controller;

import com.academia.bjj.auditoria.dto.AuditoriaResponse;
import com.academia.bjj.auditoria.model.Auditoria;
import com.academia.bjj.auditoria.repository.AuditoriaRepository;
import com.academia.bjj.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta do log de auditoria (RF-095). Apenas ADMIN.
 */
@RestController
@RequestMapping("/api/v1/auditoria")
@Tag(name = "Auditoria", description = "Log de acoes sensiveis")
public class AuditoriaController {

    private final AuditoriaRepository repository;

    public AuditoriaController(AuditoriaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public PageResponse<AuditoriaResponse> listar(@PageableDefault(size = 30) Pageable pageable) {
        return PageResponse.from(repository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse));
    }

    private AuditoriaResponse toResponse(Auditoria a) {
        return new AuditoriaResponse(a.getId(), a.getUsuarioId(), a.getUsuarioEmail(),
                a.getMetodo(), a.getCaminho(), a.getStatus(), a.getCreatedAt());
    }
}
