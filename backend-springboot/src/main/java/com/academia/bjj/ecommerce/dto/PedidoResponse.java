package com.academia.bjj.ecommerce.dto;

import com.academia.bjj.ecommerce.model.StatusPedido;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        String numero,
        StatusPedido status,
        BigDecimal subtotal,
        BigDecimal frete,
        BigDecimal desconto,
        BigDecimal total,
        String cupomCodigo,
        EnderecoResponse endereco,
        String rastreio,
        List<ItemPedidoResponse> itens,
        OffsetDateTime criadoEm
) {
    public record EnderecoResponse(
            String cep, String logradouro, String numero, String complemento,
            String bairro, String cidade, String uf) {
    }
}
