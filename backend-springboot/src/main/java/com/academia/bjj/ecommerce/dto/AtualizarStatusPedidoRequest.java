package com.academia.bjj.ecommerce.dto;

import com.academia.bjj.ecommerce.model.StatusPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarStatusPedidoRequest(
        @NotNull(message = "O status e obrigatorio")
        StatusPedido status,

        @Size(max = 60)
        String rastreio
) {
}
