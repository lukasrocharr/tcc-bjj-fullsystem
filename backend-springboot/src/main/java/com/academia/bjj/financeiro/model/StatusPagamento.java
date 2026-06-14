package com.academia.bjj.financeiro.model;

/** Resultado de uma tentativa de pagamento (via gateway ou manual). */
public enum StatusPagamento {
    PENDENTE,
    APROVADO,
    RECUSADO
}
