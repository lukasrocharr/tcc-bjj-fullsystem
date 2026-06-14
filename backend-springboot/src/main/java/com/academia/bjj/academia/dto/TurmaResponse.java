package com.academia.bjj.academia.dto;

import com.academia.bjj.academia.model.DiaSemana;
import com.academia.bjj.academia.model.NivelTurma;

import java.time.LocalTime;

public record TurmaResponse(
        Long id,
        String nome,
        ModalidadeRef modalidade,
        ProfessorRef professor,
        DiaSemana diaSemana,
        LocalTime horaInicio,
        LocalTime horaFim,
        int capacidade,
        long vagasOcupadas,
        NivelTurma nivel,
        boolean ativo
) {
    public long vagasDisponiveis() {
        return Math.max(0, capacidade - vagasOcupadas);
    }
}
