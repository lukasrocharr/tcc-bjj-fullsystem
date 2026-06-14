package com.academia.bjj.academia.dto;

public record ProfessorResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String faixa,
        String bio,
        boolean ativo,
        Long usuarioId
) {
}
