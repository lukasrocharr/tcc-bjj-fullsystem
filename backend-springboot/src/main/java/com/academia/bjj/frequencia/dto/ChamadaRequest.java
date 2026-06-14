package com.academia.bjj.frequencia.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

/**
 * Chamada em lote feita pelo professor (RF-058). Se {@code data} for nula,
 * usa a data atual.
 */
public record ChamadaRequest(
        @NotNull(message = "A turma e obrigatoria")
        Long turmaId,

        LocalDate data,

        @NotEmpty(message = "Informe ao menos um aluno presente")
        Set<Long> alunoIds
) {
}
