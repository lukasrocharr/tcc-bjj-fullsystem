package com.academia.bjj.ecommerce.dto;

import java.math.BigDecimal;

public record VariacaoResponse(
        Long id,
        String sku,
        String tamanho,
        String cor,
        BigDecimal precoAdicional,
        BigDecimal precoEfetivo,
        int estoque
) {
}
