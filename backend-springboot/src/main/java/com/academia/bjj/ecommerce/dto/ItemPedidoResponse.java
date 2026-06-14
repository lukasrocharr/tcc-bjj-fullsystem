package com.academia.bjj.ecommerce.dto;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        String nomeProduto,
        String sku,
        BigDecimal precoUnitario,
        int quantidade,
        BigDecimal subtotal
) {
}
