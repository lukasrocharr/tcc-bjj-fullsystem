package com.academia.bjj.graduacao.controller;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.graduacao.dto.FaixaAtualResponse;
import com.academia.bjj.graduacao.dto.FaixaResponse;
import com.academia.bjj.graduacao.dto.GraduacaoRequest;
import com.academia.bjj.graduacao.dto.GraduacaoResponse;
import com.academia.bjj.graduacao.service.GraduacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Graduacoes: registro, historico, faixa atual, elegibilidade e certificado PDF
 * (RF-068 a RF-073).
 */
@RestController
@RequestMapping("/api/v1/graduacoes")
@Tag(name = "Graduacoes", description = "Faixas, promocoes, faixa atual e certificados")
public class GraduacaoController {

    private static final String STAFF = "hasAnyRole('ADMIN','SUPER_ADMIN','PROFESSOR')";

    private final GraduacaoService service;

    public GraduacaoController(GraduacaoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize(STAFF)
    @Operation(summary = "Registra uma promocao e atualiza a faixa atual do aluno")
    public ResponseEntity<GraduacaoResponse> registrar(@Valid @RequestBody GraduacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @GetMapping("/faixas")
    @Operation(summary = "Lista o dominio de faixas (catalogo publico)")
    public List<FaixaResponse> faixas() {
        return service.faixas();
    }

    @GetMapping("/aluno/{alunoId}")
    public List<GraduacaoResponse> historico(@PathVariable Long alunoId) {
        return service.historico(alunoId);
    }

    @GetMapping("/aluno/{alunoId}/faixa-atual")
    @Operation(summary = "Faixa atual derivada da ultima graduacao + tempo na faixa")
    public FaixaAtualResponse faixaAtual(@PathVariable Long alunoId) {
        return service.faixaAtual(alunoId);
    }

    @GetMapping("/elegiveis")
    @PreAuthorize(STAFF)
    @Operation(summary = "Alunos elegiveis a nova graduacao por tempo na faixa")
    public List<AlunoRef> elegiveis() {
        return service.elegiveis();
    }

    @GetMapping("/{id}/certificado")
    @PreAuthorize(STAFF)
    @Operation(summary = "Gera o certificado de graduacao em PDF")
    public ResponseEntity<byte[]> certificado(@PathVariable Long id) {
        byte[] pdf = service.gerarCertificado(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=certificado-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
