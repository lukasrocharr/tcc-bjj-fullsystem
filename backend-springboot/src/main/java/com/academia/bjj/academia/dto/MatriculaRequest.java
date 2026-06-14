package com.academia.bjj.academia.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record MatriculaRequest(
        @NotNull(message = "O aluno e obrigatorio")
        Long alunoId,

        @NotNull(message = "O plano e obrigatorio")
        Long planoId,

        @NotNull(message = "A data de inicio e obrigatoria")
        LocalDate dataInicio,

        @Size(max = 500)
        String observacao,

        @NotEmpty(message = "Selecione ao menos uma turma")
        Set<Long> turmaIds
) {
}
