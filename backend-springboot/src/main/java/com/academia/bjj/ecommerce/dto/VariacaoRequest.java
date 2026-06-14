package com.academia.bjj.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VariacaoRequest(
        @NotBlank(message = "O SKU e obrigatorio")
        @Size(max = 60)
        String sku,

        @Size(max = 20)
        String tamanho,

        @Size(max = 30)
        String cor,

        @PositiveOrZero(message = "O preco adicional nao pode ser negativo")
        BigDecimal precoAdicional,

        @PositiveOrZero(message = "O estoque nao pode ser negativo")
        int estoque
) {
}
