package com.academia.bjj.financeiro.dto;

import com.academia.bjj.financeiro.model.MetodoPagamento;
import com.academia.bjj.financeiro.model.StatusPagamento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PagamentoResponse(
        Long id,
        Long mensalidadeId,
        BigDecimal valor,
        MetodoPagamento metodo,
        StatusPagamento status,
        String gatewayId,
        OffsetDateTime dataHora
) {
}
