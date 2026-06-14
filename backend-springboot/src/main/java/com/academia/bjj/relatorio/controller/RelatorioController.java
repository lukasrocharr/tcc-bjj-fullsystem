package com.academia.bjj.relatorio.controller;

import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.financeiro.model.Mensalidade;
import com.academia.bjj.financeiro.repository.MensalidadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Exportacao de relatorios em CSV (RF-094). Excel/PDF podem ser adicionados
 * reaproveitando os mesmos dados.
 */
@RestController
@RequestMapping("/api/v1/relatorios")
@Tag(name = "Relatorios", description = "Exportacao de relatorios (CSV)")
public class RelatorioController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";

    private final AlunoRepository alunoRepository;
    private final MensalidadeRepository mensalidadeRepository;

    public RelatorioController(AlunoRepository alunoRepository, MensalidadeRepository mensalidadeRepository) {
        this.alunoRepository = alunoRepository;
        this.mensalidadeRepository = mensalidadeRepository;
    }

    @GetMapping("/alunos.csv")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Exporta os alunos em CSV")
    public ResponseEntity<byte[]> alunosCsv() {
        StringBuilder sb = new StringBuilder("id;nome;email;telefone;faixaAtual;ativo\n");
        for (Aluno a : alunoRepository.findAll()) {
            sb.append(a.getId()).append(';')
                    .append(csv(a.getNome())).append(';')
                    .append(csv(a.getEmail())).append(';')
                    .append(csv(a.getTelefone())).append(';')
                    .append(csv(a.getFaixaAtual())).append(';')
                    .append(a.isAtivo()).append('\n');
        }
        return csvResponse("alunos.csv", sb.toString());
    }

    @GetMapping("/mensalidades.csv")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Exporta mensalidades em CSV (ano/mes opcionais)")
    public ResponseEntity<byte[]> mensalidadesCsv(@RequestParam(required = false) Integer ano,
                                                  @RequestParam(required = false) Integer mes) {
        List<Mensalidade> base = (ano != null && mes != null)
                ? mensalidadeRepository.findByAnoAndMes(ano, mes)
                : mensalidadeRepository.findAll();

        StringBuilder sb = new StringBuilder("id;aluno;competencia;valor;multa;juros;total;status;vencimento;pagamento\n");
        for (Mensalidade m : base) {
            sb.append(m.getId()).append(';')
                    .append(csv(m.getMatricula().getAluno().getNome())).append(';')
                    .append(String.format("%02d/%d", m.getMes(), m.getAno())).append(';')
                    .append(m.getValor()).append(';')
                    .append(m.getMulta()).append(';')
                    .append(m.getJuros()).append(';')
                    .append(m.getValorTotal()).append(';')
                    .append(m.getStatus()).append(';')
                    .append(m.getDataVencimento()).append(';')
                    .append(m.getDataPagamento() != null ? m.getDataPagamento() : "").append('\n');
        }
        return csvResponse("mensalidades.csv", sb.toString());
    }

    private String csv(String v) {
        if (v == null) {
            return "";
        }
        String escaped = v.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private ResponseEntity<byte[]> csvResponse(String nome, String conteudo) {
        byte[] body = conteudo.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nome)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(body);
    }
}
