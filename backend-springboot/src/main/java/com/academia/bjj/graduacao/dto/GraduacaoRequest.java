package com.academia.bjj.graduacao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GraduacaoRequest(
        @NotNull(message = "O aluno e obrigatorio")
        Long alunoId,

        @NotNull(message = "A faixa e obrigatoria")
        Long faixaId,

        @PositiveOrZero(message = "Os graus nao podem ser negativos")
        int graus,

        LocalDate data,

        Long professorId,

        @Size(max = 500)
        String observacao
) {
}
