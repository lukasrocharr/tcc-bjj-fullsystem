package com.academia.bjj.frequencia.dto;

import java.time.LocalDate;

/**
 * Indicadores de frequencia do aluno (RF-059, RF-060).
 */
public record FrequenciaResponse(
        Long alunoId,
        long totalCheckIns,
        long checkInsNoMes,
        long diasDistintos,
        LocalDate ultimoCheckIn,
        long streakDias
) {
}
