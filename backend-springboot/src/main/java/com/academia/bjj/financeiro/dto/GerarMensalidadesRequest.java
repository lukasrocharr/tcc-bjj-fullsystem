package com.academia.bjj.financeiro.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Competencia (mes/ano) para geracao de mensalidades. Se nula no controller,
 * usa a competencia atual.
 */
public record GerarMensalidadesRequest(
        @NotNull @Min(2000) @Max(2100)
        Integer ano,

        @NotNull @Min(1) @Max(12)
        Integer mes
) {
}
