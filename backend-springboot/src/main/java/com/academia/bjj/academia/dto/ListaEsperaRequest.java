package com.academia.bjj.academia.dto;

import jakarta.validation.constraints.NotNull;

public record ListaEsperaRequest(
        @NotNull(message = "A turma e obrigatoria")
        Long turmaId,

        @NotNull(message = "O aluno e obrigatorio")
        Long alunoId
) {
}
