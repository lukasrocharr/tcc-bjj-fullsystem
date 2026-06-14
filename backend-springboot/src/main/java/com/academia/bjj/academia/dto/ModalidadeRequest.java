package com.academia.bjj.academia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModalidadeRequest(
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 80)
        String nome,

        @Size(max = 500)
        String descricao,

        Boolean ativo
) {
}
