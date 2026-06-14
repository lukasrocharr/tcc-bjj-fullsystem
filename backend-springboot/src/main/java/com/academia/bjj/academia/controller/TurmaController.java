package com.academia.bjj.academia.controller;

import com.academia.bjj.academia.dto.TurmaRequest;
import com.academia.bjj.academia.dto.TurmaResponse;
import com.academia.bjj.academia.service.TurmaService;
import com.academia.bjj.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/turmas")
@Tag(name = "Turmas", description = "Gestao de turmas e grade de horarios (RF-045, RF-051)")
public class TurmaController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";

    private final TurmaService service;

    public TurmaController(TurmaService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<TurmaResponse> listar(
            @RequestParam(required = false) Long modalidadeId,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.listar(modalidadeId, ativo, pageable);
    }

    @GetMapping("/grade")
    @Operation(summary = "Grade de horarios completa, ordenada por dia e horario")
    public List<TurmaResponse> grade() {
        return service.grade();
    }

    @GetMapping("/{id}")
    public TurmaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @PreAuthorize(ADMIN)
    public ResponseEntity<TurmaResponse> criar(@Valid @RequestBody TurmaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN)
    public TurmaResponse atualizar(@PathVariable Long id, @Valid @RequestBody TurmaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
