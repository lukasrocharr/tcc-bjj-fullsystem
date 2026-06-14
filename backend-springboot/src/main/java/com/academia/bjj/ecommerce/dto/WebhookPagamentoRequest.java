package com.academia.bjj.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Webhook de confirmacao de pagamento do gateway (RF-030). Em producao seria
 * autenticado por assinatura; no mock aceita o numero do pedido + resultado.
 */
public record WebhookPagamentoRequest(
        @NotBlank(message = "O numero do pedido e obrigatorio")
        String numeroPedido,

        String gatewayId,

        boolean aprovado
) {
}
