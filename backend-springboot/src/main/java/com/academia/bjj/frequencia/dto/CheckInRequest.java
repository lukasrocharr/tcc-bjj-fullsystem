package com.academia.bjj.frequencia.dto;

import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotNull(message = "O aluno e obrigatorio")
        Long alunoId,

        @NotNull(message = "A turma e obrigatoria")
        Long turmaId
) {
}
