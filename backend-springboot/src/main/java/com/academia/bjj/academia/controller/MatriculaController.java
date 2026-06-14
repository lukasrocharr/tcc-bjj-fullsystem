package com.academia.bjj.academia.controller;

import com.academia.bjj.academia.dto.ListaEsperaRequest;
import com.academia.bjj.academia.dto.ListaEsperaResponse;
import com.academia.bjj.academia.dto.MatriculaRequest;
import com.academia.bjj.academia.dto.MatriculaResponse;
import com.academia.bjj.academia.dto.StatusMatriculaRequest;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.academia.service.MatriculaService;
import com.academia.bjj.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Gestao de matriculas e lista de espera (RF-046 a RF-048).
 * Leitura para ADMIN/PROFESSOR; escrita apenas ADMIN.
 */
@RestController
@RequestMapping("/api/v1/matriculas")
@Tag(name = "Matriculas", description = "Matriculas, status e lista de espera (RF-046 a RF-048)")
public class MatriculaController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";
    private static final String STAFF = "hasAnyRole('ADMIN','SUPER_ADMIN','PROFESSOR')";

    private final MatriculaService service;

    public MatriculaController(MatriculaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize(STAFF)
    public PageResponse<MatriculaResponse> listar(
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) StatusMatricula status,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.listar(alunoId, status, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize(STAFF)
    public MatriculaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @PreAuthorize(ADMIN)
    public ResponseEntity<MatriculaResponse> criar(@Valid @RequestBody MatriculaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Altera o status da matricula (ATIVA/SUSPENSA/CANCELADA)")
    public MatriculaResponse alterarStatus(@PathVariable Long id,
                                           @Valid @RequestBody StatusMatriculaRequest request) {
        return service.alterarStatus(id, request.status());
    }

    @PostMapping("/lista-espera")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Adiciona um aluno a lista de espera de uma turma lotada")
    public ResponseEntity<ListaEsperaResponse> entrarListaEspera(
            @Valid @RequestBody ListaEsperaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.entrarListaEspera(request.turmaId(), request.alunoId()));
    }

    @GetMapping("/lista-espera/{turmaId}")
    @PreAuthorize(STAFF)
    public List<ListaEsperaResponse> listaEspera(@PathVariable Long turmaId) {
        return service.listaEspera(turmaId);
    }
}
