package com.academia.bjj.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarrinhoResponse(
        Long id,
        List<ItemCarrinhoResponse> itens,
        int totalItens,
        BigDecimal subtotal
) {
}
