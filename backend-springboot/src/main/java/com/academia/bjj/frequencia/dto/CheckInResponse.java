package com.academia.bjj.frequencia.dto;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.academia.dto.TurmaRef;
import com.academia.bjj.frequencia.model.OrigemCheckIn;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record CheckInResponse(
        Long id,
        AlunoRef aluno,
        TurmaRef turma,
        LocalDate data,
        OffsetDateTime dataHora,
        OrigemCheckIn origem
) {
}
