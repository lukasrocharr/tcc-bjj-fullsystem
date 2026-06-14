package com.academia.bjj.relatorio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracao da Fase 6: dashboard, comunicado em massa -> notificacao in-app,
 * log de auditoria e exportacao CSV (RF-091 a RF-095, RF-103, RF-104).
 */
@SpringBootTest
@AutoConfigureMockMvc
class RefinamentoIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String login(String email, String senha) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("accessToken").asText();
    }

    @Test
    void dashboard_retornaIndicadores() throws Exception {
        String admin = login("admin@bjj.local", "Admin@123");
        mockMvc.perform(get("/api/v1/dashboard").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alunosAtivos").exists())
                .andExpect(jsonPath("$.receitaTotalMes").exists())
                .andExpect(jsonPath("$.serieReceita.length()").value(6));
    }

    @Test
    void dashboard_semAdmin_proibido() throws Exception {
        String aluno = login("aluno@bjj.local", "Aluno@123");
        mockMvc.perform(get("/api/v1/dashboard").header("Authorization", "Bearer " + aluno))
                .andExpect(status().isForbidden());
    }

    @Test
    void comunicado_geraNotificacaoInApp_eMarcaComoLida() throws Exception {
        String admin = login("admin@bjj.local", "Admin@123");

        mockMvc.perform(post("/api/v1/notificacoes/comunicados")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Aviso","mensagem":"Treino especial sabado","papel":"ALUNO","enviarEmail":false}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destinatarios").value(greaterThanOrEqualTo(1)));

        String aluno = login("aluno@bjj.local", "Aluno@123");
        mockMvc.perform(get("/api/v1/notificacoes/nao-lidas/contagem")
                        .header("Authorization", "Bearer " + aluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naoLidas").value(greaterThanOrEqualTo(1)));

        mockMvc.perform(patch("/api/v1/notificacoes/lidas")
                        .header("Authorization", "Bearer " + aluno))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/notificacoes/nao-lidas/contagem")
                        .header("Authorization", "Bearer " + aluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naoLidas").value(0));
    }

    @Test
    void auditoria_registraAcaoMutante() throws Exception {
        String admin = login("admin@bjj.local", "Admin@123");

        // Acao mutante autenticada: criar categoria
        mockMvc.perform(post("/api/v1/loja/categorias")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Cat Auditoria\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/auditoria").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.content[0].metodo").exists());
    }

    @Test
    void exportaAlunosCsv() throws Exception {
        String admin = login("admin@bjj.local", "Admin@123");
        mockMvc.perform(get("/api/v1/relatorios/alunos.csv").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("alunos.csv")))
                .andExpect(content().string(containsString("id;nome;email")));
    }
}
