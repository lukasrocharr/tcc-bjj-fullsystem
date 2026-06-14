package com.academia.bjj.academia.dto;

import com.academia.bjj.academia.model.StatusMatricula;

import java.time.LocalDate;
import java.util.List;

public record MatriculaResponse(
        Long id,
        AlunoRef aluno,
        PlanoRef plano,
        StatusMatricula status,
        LocalDate dataInicio,
        LocalDate dataFim,
        String observacao,
        List<TurmaRef> turmas
) {
}
