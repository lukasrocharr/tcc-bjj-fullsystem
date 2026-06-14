package com.academia.bjj.academia.dto;

import java.time.LocalDate;

public record AlunoResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String cpf,
        String contatoEmergencia,
        String observacoesSaude,
        String faixaAtual,
        boolean ativo,
        Long usuarioId
) {
}
