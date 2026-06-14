package com.academia.bjj.graduacao;

import com.fasterxml.jackson.databind.JsonNode;
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
 * Integracao da graduacao (RF-070, RF-071, RF-073): registrar promocao atualiza
 * a faixa atual derivada e o certificado PDF e gerado. O catalogo de faixas e publico.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GraduacaoIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String loginAdmin() throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@bjj.local\",\"senha\":\"Admin@123\"}"))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private long criarAluno(String token, String email) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/alunos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Aluno Grad\",\"email\":\"" + email + "\"}"))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    private long primeiraFaixaId() throws Exception {
        MvcResult r = mockMvc.perform(get("/api/v1/graduacoes/faixas"))
                .andExpect(status().isOk()).andReturn();
        JsonNode faixas = objectMapper.readTree(r.getResponse().getContentAsString());
        return faixas.get(0).get("id").asLong();
    }

    @Test
    void faixas_saoPublicas() throws Exception {
        mockMvc.perform(get("/api/v1/graduacoes/faixas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(10)));
    }

    @Test
    void registrarGraduacao_atualizaFaixaAtual_eGeraCertificado() throws Exception {
        String token = loginAdmin();
        long alunoId = criarAluno(token, "aluno.grad@bjj.local");
        long faixaId = primeiraFaixaId();

        MvcResult grad = mockMvc.perform(post("/api/v1/graduacoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoId":%d,"faixaId":%d,"graus":1,"data":"2026-06-13","observacao":"ok"}
                                """.formatted(alunoId, faixaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.faixa.id").value((int) faixaId))
                .andReturn();
        long gradId = objectMapper.readTree(grad.getResponse().getContentAsString()).get("id").asLong();

        // Faixa atual refletida imediatamente
        mockMvc.perform(get("/api/v1/graduacoes/aluno/" + alunoId + "/faixa-atual")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faixa").exists())
                .andExpect(jsonPath("$.graus").value(1));

        // Historico preservado
        mockMvc.perform(get("/api/v1/graduacoes/aluno/" + alunoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Certificado PDF
        mockMvc.perform(get("/api/v1/graduacoes/" + gradId + "/certificado")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    byte[] body = result.getResponse().getContentAsByteArray();
                    if (body.length < 4 || body[0] != '%' || body[1] != 'P'
                            || body[2] != 'D' || body[3] != 'F') {
                        throw new AssertionError("Resposta nao e um PDF valido");
                    }
                });
    }
}
