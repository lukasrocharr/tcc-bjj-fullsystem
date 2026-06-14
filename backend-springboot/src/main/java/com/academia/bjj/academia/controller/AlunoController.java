package com.academia.bjj.academia.controller;

import com.academia.bjj.academia.dto.AlunoRequest;
import com.academia.bjj.academia.dto.AlunoResponse;
import com.academia.bjj.academia.service.AlunoService;
import com.academia.bjj.auth.security.JwtAuthenticationFilter.AuthenticatedUser;
import com.academia.bjj.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

/**
 * Gestao de alunos (RF-041). Leitura para ADMIN/PROFESSOR; escrita apenas ADMIN.
 */
@RestController
@RequestMapping("/api/v1/alunos")
@Tag(name = "Alunos", description = "Gestao de alunos (RF-041)")
public class AlunoController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";
    private static final String STAFF = "hasAnyRole('ADMIN','SUPER_ADMIN','PROFESSOR')";

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize(STAFF)
    public PageResponse<AlunoResponse> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @GetMapping("/me")
    @Operation(summary = "Cadastro de aluno do usuario autenticado (portal do aluno)")
    public AlunoResponse meuPerfil(@AuthenticationPrincipal AuthenticatedUser user) {
        return service.meuPerfil(user.id());
    }

    @GetMapping("/{id}")
    @PreAuthorize(STAFF)
    public AlunoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @PreAuthorize(ADMIN)
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody AlunoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN)
    public AlunoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
