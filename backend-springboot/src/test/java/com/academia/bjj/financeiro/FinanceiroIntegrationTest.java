package com.academia.bjj.financeiro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracao do financeiro (RF-074 a RF-082): geracao de mensalidade ->
 * pagamento aprovado (gateway mock) -> PAGA -> recibo PDF -> relatorio.
 * Tambem verifica RBAC (PROFESSOR nao gera mensalidades).
 */
@SpringBootTest
@AutoConfigureMockMvc
class FinanceiroIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String login(String email, String senha) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private long postId(String token, String url, String body) throws Exception {
        MvcResult r = mockMvc.perform(post(url)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void gerarMensalidades_semAdmin_proibido() throws Exception {
        String prof = login("professor@bjj.local", "Professor@123");
        mockMvc.perform(post("/api/v1/financeiro/mensalidades/gerar")
                        .header("Authorization", "Bearer " + prof)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ano\":2026,\"mes\":7}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void fluxoFinanceiro_geracaoPagamentoReciboRelatorio() throws Exception {
        String token = login("admin@bjj.local", "Admin@123");

        long modalidadeId = postId(token, "/api/v1/modalidades", "{\"nome\":\"Mod Fin\"}");
        long planoId = postId(token, "/api/v1/planos",
                "{\"nome\":\"Plano Fin\",\"valor\":150.0,\"periodicidade\":\"MENSAL\",\"aulasPorSemana\":2}");
        long turmaId = postId(token, "/api/v1/turmas",
                ("{\"nome\":\"Turma Fin\",\"modalidadeId\":%d,\"diaSemana\":\"SEGUNDA\","
                        + "\"horaInicio\":\"19:00:00\",\"horaFim\":\"20:00:00\",\"capacidade\":10,\"nivel\":\"INICIANTE\"}")
                        .formatted(modalidadeId));
        long alunoId = postId(token, "/api/v1/alunos",
                "{\"nome\":\"Aluno Fin\",\"email\":\"aluno.fin@bjj.local\"}");
        postId(token, "/api/v1/matriculas",
                ("{\"alunoId\":%d,\"planoId\":%d,\"dataInicio\":\"2026-06-01\",\"turmaIds\":[%d]}")
                        .formatted(alunoId, planoId, turmaId));

        // Geracao da competencia 07/2026 (vencimento futuro -> sem encargos)
        mockMvc.perform(post("/api/v1/financeiro/mensalidades/gerar")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ano\":2026,\"mes\":7}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.geradas").value(greaterThanOrEqualTo(1)));

        // Localiza a mensalidade do aluno
        MvcResult lista = mockMvc.perform(get("/api/v1/financeiro/mensalidades")
                        .header("Authorization", "Bearer " + token)
                        .param("alunoId", String.valueOf(alunoId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDENTE"))
                .andReturn();
        long mensId = objectMapper.readTree(lista.getResponse().getContentAsString())
                .get("content").get(0).get("id").asLong();

        // Pagamento via PIX (gateway mock aprova)
        mockMvc.perform(post("/api/v1/financeiro/mensalidades/" + mensId + "/pagar")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"metodo\":\"PIX\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));

        // Mensalidade agora PAGA
        mockMvc.perform(get("/api/v1/financeiro/mensalidades/" + mensId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGA"))
                .andExpect(jsonPath("$.valorPago").value(150.0));

        // Recibo em PDF
        mockMvc.perform(get("/api/v1/financeiro/mensalidades/" + mensId + "/recibo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    byte[] b = result.getResponse().getContentAsByteArray();
                    if (b.length < 4 || b[0] != '%' || b[1] != 'P' || b[2] != 'D' || b[3] != 'F') {
                        throw new AssertionError("Resposta nao e um PDF valido");
                    }
                });

        // Relatorio da competencia reflete o recebido
        mockMvc.perform(get("/api/v1/financeiro/relatorio")
                        .header("Authorization", "Bearer " + token)
                        .param("ano", "2026").param("mes", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecebido").value(greaterThanOrEqualTo(150.0)))
                .andExpect(jsonPath("$.qtdPagas").value(greaterThanOrEqualTo(1)));
    }
}
