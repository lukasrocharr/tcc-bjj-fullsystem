package com.academia.bjj.financeiro.dto;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.financeiro.model.StatusMensalidade;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MensalidadeResponse(
        Long id,
        AlunoRef aluno,
        String plano,
        int ano,
        int mes,
        BigDecimal valor,
        BigDecimal multa,
        BigDecimal juros,
        BigDecimal valorTotal,
        BigDecimal valorPago,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        StatusMensalidade status
) {
}
