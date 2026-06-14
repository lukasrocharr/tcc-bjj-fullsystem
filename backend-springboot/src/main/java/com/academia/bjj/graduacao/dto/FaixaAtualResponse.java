package com.academia.bjj.graduacao.dto;

import java.time.LocalDate;

/**
 * Faixa atual derivada da ultima graduacao (RF-071) + tempo na faixa.
 */
public record FaixaAtualResponse(
        Long alunoId,
        String faixa,
        int graus,
        LocalDate desde,
        long diasNaFaixa
) {
}
