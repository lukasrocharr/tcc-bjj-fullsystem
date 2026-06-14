package com.academia.bjj.academia;

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
 * Integracao do nucleo da academia: catalogo publico, RBAC nas escritas e
 * criterio de aceitacao do SRS (PROFESSOR -> 403 em endpoint exclusivo de ADMIN).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AcademiaIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String login(String email, String senha) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","senha":"%s"}
                                """.formatted(email, senha)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("accessToken").asText();
    }

    @Test
    void catalogoModalidades_ePublico_eTrazSeed() throws Exception {
        // >= 4: as 4 modalidades do seed (outros testes podem ter criado mais,
        // pois o H2 em memoria e compartilhado no contexto de teste).
        mockMvc.perform(get("/api/v1/modalidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.content[0].nome").exists());
    }

    @Test
    void criarModalidade_semToken_retorna401() throws Exception {
        mockMvc.perform(post("/api/v1/modalidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Nova Modalidade"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criarModalidade_comProfessor_retorna403() throws Exception {
        String token = login("professor@bjj.local", "Professor@123");
        mockMvc.perform(post("/api/v1/modalidades")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Tentativa Professor"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void fluxoAcademia_comAdmin_criaModalidadePlanoTurmaAlunoEMatricula() throws Exception {
        String token = login("admin@bjj.local", "Admin@123");

        // Modalidade
        long modalidadeId = idDe(mockMvc.perform(post("/api/v1/modalidades")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Modalidade IT","descricao":"teste"}
                                """))
                .andExpect(status().isCreated())
                .andReturn());

        // Plano
        long planoId = idDe(mockMvc.perform(post("/api/v1/planos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Plano IT","valor":100.0,"periodicidade":"MENSAL","aulasPorSemana":2,
                                 "modalidadeIds":[%d]}
                                """.formatted(modalidadeId)))
                .andExpect(status().isCreated())
                .andReturn());

        // Turma (capacidade 1 para exercitar lotacao depois)
        long turmaId = idDe(mockMvc.perform(post("/api/v1/turmas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Turma IT","modalidadeId":%d,"diaSemana":"SEGUNDA",
                                 "horaInicio":"19:00:00","horaFim":"20:00:00","capacidade":1,"nivel":"INICIANTE"}
                                """.formatted(modalidadeId)))
                .andExpect(status().isCreated())
                .andReturn());

        // Aluno
        long alunoId = idDe(mockMvc.perform(post("/api/v1/alunos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Aluno IT","email":"aluno.it@bjj.local"}
                                """))
                .andExpect(status().isCreated())
                .andReturn());

        // Matricula
        mockMvc.perform(post("/api/v1/matriculas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoId":%d,"planoId":%d,"dataInicio":"2026-06-01","turmaIds":[%d]}
                                """.formatted(alunoId, planoId, turmaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVA"))
                .andExpect(jsonPath("$.turmas[0].id").value((int) turmaId));

        // Turma agora deve constar 1 vaga ocupada (capacidade 1 -> lotada)
        mockMvc.perform(get("/api/v1/turmas/" + turmaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacidade").value(1))
                .andExpect(jsonPath("$.vagasOcupadas").value(1));
    }

    private long idDe(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
