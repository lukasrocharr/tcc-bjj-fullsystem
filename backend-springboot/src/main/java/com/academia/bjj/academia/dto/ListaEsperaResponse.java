package com.academia.bjj.academia.dto;

public record ListaEsperaResponse(
        Long id,
        Long turmaId,
        AlunoRef aluno,
        int posicao
) {
}
