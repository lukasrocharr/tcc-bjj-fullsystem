package com.academia.bjj.financeiro.controller;

import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.auth.security.JwtAuthenticationFilter.AuthenticatedUser;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.financeiro.dto.GerarMensalidadesRequest;
import com.academia.bjj.financeiro.dto.GerarMensalidadesResponse;
import com.academia.bjj.financeiro.dto.MensalidadeResponse;
import com.academia.bjj.financeiro.dto.PagamentoRequest;
import com.academia.bjj.financeiro.dto.PagamentoResponse;
import com.academia.bjj.financeiro.dto.RelatorioFinanceiroResponse;
import com.academia.bjj.financeiro.model.StatusMensalidade;
import com.academia.bjj.financeiro.service.MensalidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Financeiro: mensalidades, pagamentos, recibos e relatorio (RF-074 a RF-082).
 */
@RestController
@RequestMapping("/api/v1/financeiro")
@Tag(name = "Financeiro", description = "Mensalidades, pagamentos, recibos e relatorios")
public class FinanceiroController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";
    private static final String STAFF = "hasAnyRole('ADMIN','SUPER_ADMIN','PROFESSOR')";

    private final MensalidadeService service;
    private final AlunoRepository alunoRepository;

    public FinanceiroController(MensalidadeService service, AlunoRepository alunoRepository) {
        this.service = service;
        this.alunoRepository = alunoRepository;
    }

    // ----- Self-service do aluno (portal do aluno) -----

    @GetMapping("/me/mensalidades")
    @Operation(summary = "Mensalidades do aluno autenticado")
    public PageResponse<MensalidadeResponse> minhasMensalidades(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 50) Pageable pageable) {
        return service.listar(alunoIdDoUsuario(user), null, pageable);
    }

    @GetMapping("/me/mensalidades/{id}/recibo")
    @Operation(summary = "Recibo de uma mensalidade do proprio aluno")
    public ResponseEntity<byte[]> meuRecibo(@AuthenticationPrincipal AuthenticatedUser user,
                                            @PathVariable Long id) {
        Long alunoId = alunoIdDoUsuario(user);
        if (!service.buscar(id).aluno().id().equals(alunoId)) {
            throw new BusinessException("Esta mensalidade nao pertence ao aluno autenticado");
        }
        byte[] pdf = service.recibo(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=recibo-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private Long alunoIdDoUsuario(AuthenticatedUser user) {
        return alunoRepository.findByUsuarioId(user.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhum cadastro de aluno vinculado a este usuario"))
                .getId();
    }

    @PostMapping("/mensalidades/gerar")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Gera mensalidades da competencia (1 por matricula ativa, sem duplicar)")
    public ResponseEntity<GerarMensalidadesResponse> gerar(@Valid @RequestBody GerarMensalidadesRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerar(req.ano(), req.mes()));
    }

    @GetMapping("/mensalidades")
    @PreAuthorize(STAFF)
    public PageResponse<MensalidadeResponse> listar(
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) StatusMensalidade status,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.listar(alunoId, status, pageable);
    }

    @GetMapping("/mensalidades/{id}")
    @PreAuthorize(STAFF)
    public MensalidadeResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping("/mensalidades/{id}/pagar")
    @PreAuthorize(STAFF)
    @Operation(summary = "Registra pagamento (gateway mock); aprova -> marca PAGA")
    public PagamentoResponse pagar(@PathVariable Long id, @Valid @RequestBody PagamentoRequest req) {
        return service.pagar(id, req.metodo());
    }

    @PostMapping("/mensalidades/{id}/cancelar")
    @PreAuthorize(ADMIN)
    public MensalidadeResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @GetMapping("/mensalidades/{id}/recibo")
    @PreAuthorize(STAFF)
    @Operation(summary = "Recibo de mensalidade paga em PDF")
    public ResponseEntity<byte[]> recibo(@PathVariable Long id) {
        byte[] pdf = service.recibo(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=recibo-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/atualizar-atrasadas")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Reprocessa mensalidades vencidas (marca ATRASADA + multa/juros)")
    public Map<String, Integer> atualizarAtrasadas() {
        return Map.of("processadas", service.atualizarAtrasadas());
    }

    @GetMapping("/aluno/{alunoId}/bloqueado")
    @PreAuthorize(STAFF)
    @Operation(summary = "Indica se o aluno esta bloqueado por inadimplencia")
    public Map<String, Boolean> bloqueado(@PathVariable Long alunoId) {
        return Map.of("bloqueado", service.alunoBloqueado(alunoId));
    }

    @GetMapping("/relatorio")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Relatorio financeiro consolidado (ano/mes opcionais)")
    public RelatorioFinanceiroResponse relatorio(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        return service.relatorio(ano, mes);
    }
}
