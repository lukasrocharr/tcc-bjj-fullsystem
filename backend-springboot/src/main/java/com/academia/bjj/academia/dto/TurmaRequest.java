package com.academia.bjj.academia.dto;

import com.academia.bjj.academia.model.DiaSemana;
import com.academia.bjj.academia.model.NivelTurma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record TurmaRequest(
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120)
        String nome,

        @NotNull(message = "A modalidade e obrigatoria")
        Long modalidadeId,

        Long professorId,

        @NotNull(message = "O dia da semana e obrigatorio")
        DiaSemana diaSemana,

        @NotNull(message = "A hora de inicio e obrigatoria")
        LocalTime horaInicio,

        @NotNull(message = "A hora de fim e obrigatoria")
        LocalTime horaFim,

        @PositiveOrZero(message = "A capacidade nao pode ser negativa")
        int capacidade,

        @NotNull(message = "O nivel e obrigatorio")
        NivelTurma nivel,

        Boolean ativo
) {
}
