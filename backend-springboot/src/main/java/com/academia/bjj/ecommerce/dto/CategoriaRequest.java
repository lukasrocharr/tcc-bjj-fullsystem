package com.academia.bjj.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 80)
        String nome,

        Boolean ativo
) {
}
