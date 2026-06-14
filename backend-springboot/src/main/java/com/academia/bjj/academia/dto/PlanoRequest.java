package com.academia.bjj.academia.dto;

import com.academia.bjj.academia.model.Periodicidade;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record PlanoRequest(
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 80)
        String nome,

        @Size(max = 500)
        String descricao,

        @NotNull(message = "O valor e obrigatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "O valor nao pode ser negativo")
        BigDecimal valor,

        @NotNull(message = "A periodicidade e obrigatoria")
        Periodicidade periodicidade,

        @PositiveOrZero(message = "Aulas por semana nao pode ser negativo")
        int aulasPorSemana,

        Boolean ativo,

        /** IDs das modalidades permitidas pelo plano. */
        Set<Long> modalidadeIds
) {
}
