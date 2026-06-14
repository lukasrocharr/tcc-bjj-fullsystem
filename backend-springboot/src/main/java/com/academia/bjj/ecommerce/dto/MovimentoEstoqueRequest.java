package com.academia.bjj.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Entrada/saida de estoque com motivo (RF-035). Quantidade positiva; o sinal e
 * dado pelo tipo (ENTRADA soma, SAIDA subtrai).
 */
public record MovimentoEstoqueRequest(
        @NotNull(message = "O tipo e obrigatorio (ENTRADA ou SAIDA)")
        Tipo tipo,

        @Positive(message = "A quantidade deve ser positiva")
        int quantidade,

        @Size(max = 200)
        String motivo
) {
    public enum Tipo { ENTRADA, SAIDA }
}
