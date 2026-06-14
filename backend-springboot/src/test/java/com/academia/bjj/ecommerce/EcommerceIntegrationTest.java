package com.academia.bjj.ecommerce;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracao do e-commerce (criterios de aceitacao do SRS):
 *  - pagamento aprovado -> pedido PAGO e estoque da variacao decrementado (RF-030);
 *  - cancelamento devolve o estoque (RF-032);
 *  - carrinho rejeita quantidade acima do estoque (RF-019);
 *  - catalogo publico; criar produto exige ADMIN.
 */
@SpringBootTest
@AutoConfigureMockMvc
class EcommerceIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String loginAdmin() throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@bjj.local\",\"senha\":\"Admin@123\"}"))
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
    void catalogoPublico_eCriarProdutoExigeAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/loja/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)));

        mockMvc.perform(post("/api/v1/loja/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"X\",\"categoriaId\":1,\"preco\":10.0}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void fluxoCompra_pagamentoAprovado_baixaEstoque_eCancelamentoDevolve() throws Exception {
        String token = loginAdmin();
        String sessao = "sess-it-ecom";

        long catId = postId(token, "/api/v1/loja/categorias", "{\"nome\":\"Cat IT Ecom\"}");
        long prodId = postId(token, "/api/v1/loja/produtos",
                ("{\"nome\":\"Produto IT\",\"categoriaId\":%d,\"preco\":100.00}").formatted(catId));
        long varId = postId(token, "/api/v1/loja/produtos/" + prodId + "/variacoes",
                "{\"sku\":\"IT-SKU-1\",\"tamanho\":\"M\",\"cor\":\"Azul\",\"precoAdicional\":0,\"estoque\":3}");

        // Visitante adiciona 2 unidades
        mockMvc.perform(post("/api/v1/loja/carrinho/itens")
                        .header("X-Session-Id", sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"variacaoId\":%d,\"quantidade\":2}").formatted(varId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItens").value(2));

        // Checkout (gateway mock aprova) -> PAGO
        MvcResult checkout = mockMvc.perform(post("/api/v1/loja/checkout")
                        .header("X-Session-Id", sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"endereco":{"cep":"01001-000","logradouro":"Rua A","numero":"10",
                                 "bairro":"Centro","cidade":"Sao Paulo","uf":"SP"},"metodo":"PIX"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAGO"))
                .andReturn();
        long pedidoId = objectMapper.readTree(checkout.getResponse().getContentAsString()).get("id").asLong();

        // Estoque decrementado: 3 - 2 = 1
        mockMvc.perform(get("/api/v1/loja/produtos/" + prodId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.variacoes[0].estoque").value(1));

        // Cancelamento devolve o estoque -> 3
        mockMvc.perform(patch("/api/v1/loja/admin/pedidos/" + pedidoId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CANCELADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        mockMvc.perform(get("/api/v1/loja/produtos/" + prodId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.variacoes[0].estoque").value(3));
    }

    @Test
    void carrinho_rejeitaQuantidadeAcimaDoEstoque() throws Exception {
        String token = loginAdmin();
        long catId = postId(token, "/api/v1/loja/categorias", "{\"nome\":\"Cat IT Estoque\"}");
        long prodId = postId(token, "/api/v1/loja/produtos",
                ("{\"nome\":\"Produto Estoque\",\"categoriaId\":%d,\"preco\":50.00}").formatted(catId));
        long varId = postId(token, "/api/v1/loja/produtos/" + prodId + "/variacoes",
                "{\"sku\":\"IT-SKU-EST\",\"estoque\":2}");

        mockMvc.perform(post("/api/v1/loja/carrinho/itens")
                        .header("X-Session-Id", "sess-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"variacaoId\":%d,\"quantidade\":5}").formatted(varId)))
                .andExpect(status().isUnprocessableEntity());
    }
}
