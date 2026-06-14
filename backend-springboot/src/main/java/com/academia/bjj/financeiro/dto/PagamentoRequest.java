package com.academia.bjj.financeiro.dto;

import com.academia.bjj.financeiro.model.MetodoPagamento;
import jakarta.validation.constraints.NotNull;

public record PagamentoRequest(
        @NotNull(message = "O metodo de pagamento e obrigatorio")
        MetodoPagamento metodo
) {
}
