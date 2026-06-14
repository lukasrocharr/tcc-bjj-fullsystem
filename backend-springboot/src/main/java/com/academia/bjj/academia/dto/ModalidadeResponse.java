package com.academia.bjj.academia.dto;

public record ModalidadeResponse(
        Long id,
        String nome,
        String descricao,
        boolean ativo
) {
}
