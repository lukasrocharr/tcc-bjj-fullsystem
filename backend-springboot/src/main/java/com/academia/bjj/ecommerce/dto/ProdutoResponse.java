package com.academia.bjj.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        CategoriaRef categoria,
        BigDecimal preco,
        boolean ativo,
        List<String> imagens,
        List<VariacaoResponse> variacoes
) {
}
