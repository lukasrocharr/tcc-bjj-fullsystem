package com.academia.bjj.financeiro.dto;

import java.math.BigDecimal;

/**
 * Relatorio financeiro consolidado (RF-082). O total da loja entra na Fase 5
 * (e-commerce); por ora fica zerado, mantendo o campo para evolucao.
 */
public record RelatorioFinanceiroResponse(
        Integer ano,
        Integer mes,
        BigDecimal totalRecebido,
        BigDecimal totalPendente,
        BigDecimal totalAtrasado,
        long qtdPagas,
        long qtdPendentes,
        long qtdAtrasadas,
        BigDecimal totalLoja,
        BigDecimal totalGeral
) {
}
