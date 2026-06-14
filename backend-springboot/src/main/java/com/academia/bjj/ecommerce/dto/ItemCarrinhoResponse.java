package com.academia.bjj.ecommerce.dto;

import java.math.BigDecimal;

public record ItemCarrinhoResponse(
        Long id,
        Long variacaoId,
        String sku,
        String produto,
        String tamanho,
        String cor,
        BigDecimal precoUnitario,
        int quantidade,
        BigDecimal subtotal,
        int estoqueDisponivel
) {
}
