package com.academia.bjj.ecommerce.model;

/** Ciclo de vida de um pedido (RF-031). */
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO,
    PAGO,
    ENVIADO,
    ENTREGUE,
    CANCELADO
}
