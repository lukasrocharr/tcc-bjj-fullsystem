package com.academia.bjj.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoRequest(
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 140)
        String nome,

        @Size(max = 2000)
        String descricao,

        @NotNull(message = "A categoria e obrigatoria")
        Long categoriaId,

        @NotNull(message = "O preco e obrigatorio")
        @PositiveOrZero(message = "O preco nao pode ser negativo")
        BigDecimal preco,

        Boolean ativo,

        List<String> imagens
) {
}
