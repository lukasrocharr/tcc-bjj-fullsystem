package com.academia.bjj.frequencia.controller;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.frequencia.dto.ChamadaRequest;
import com.academia.bjj.frequencia.dto.CheckInRequest;
import com.academia.bjj.frequencia.dto.CheckInResponse;
import com.academia.bjj.frequencia.dto.FrequenciaResponse;
import com.academia.bjj.frequencia.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
 * Frequencia: check-in self-service, chamada do professor e indicadores
 * (RF-056 a RF-062).
 */
@RestController
@RequestMapping("/api/v1/frequencia")
@Tag(name = "Frequencia", description = "Check-in, chamada e indicadores de frequencia")
public class FrequenciaController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";
    private static final String STAFF = "hasAnyRole('ADMIN','SUPER_ADMIN','PROFESSOR')";

    private final CheckInService service;

    public FrequenciaController(CheckInService service) {
        this.service = service;
    }

    @PostMapping("/check-in")
    @Operation(summary = "Check-in self-service do aluno (valida matricula e janela de horario)")
    public ResponseEntity<CheckInResponse> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PostMapping("/chamada")
    @PreAuthorize(STAFF)
    @Operation(summary = "Chamada em lote pelo professor")
    public ResponseEntity<List<CheckInResponse>> chamada(@Valid @RequestBody ChamadaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarChamada(request));
    }

    @GetMapping("/aluno/{alunoId}")
    @Operation(summary = "Indicadores de frequencia do aluno")
    public FrequenciaResponse indicadores(@PathVariable Long alunoId) {
        return service.frequenciaDoAluno(alunoId);
    }

    @GetMapping("/aluno/{alunoId}/historico")
    public List<CheckInResponse> historico(@PathVariable Long alunoId) {
        return service.historicoDoAluno(alunoId);
    }

    @GetMapping("/alertas-baixa")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Alunos ativos com baixa frequencia (sem check-in alem do limite)")
    public List<AlunoRef> alertasBaixaFrequencia() {
        return service.alertasBaixaFrequencia();
    }
}
