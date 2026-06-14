package com.academia.bjj.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddItemRequest(
        @NotNull(message = "A variacao e obrigatoria")
        Long variacaoId,

        @Positive(message = "A quantidade deve ser positiva")
        int quantidade
) {
}
