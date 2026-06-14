package com.academia.bjj.graduacao.dto;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.academia.dto.ProfessorRef;

import java.time.LocalDate;

public record GraduacaoResponse(
        Long id,
        AlunoRef aluno,
        FaixaRef faixa,
        int graus,
        LocalDate data,
        ProfessorRef professor,
        String observacao
) {
}
