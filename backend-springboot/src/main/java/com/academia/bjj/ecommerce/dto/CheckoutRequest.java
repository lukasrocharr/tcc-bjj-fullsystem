package com.academia.bjj.ecommerce.dto;

import com.academia.bjj.financeiro.model.MetodoPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull(message = "O endereco e obrigatorio")
        @Valid
        EnderecoRequest endereco,

        String cupomCodigo,

        @NotNull(message = "O metodo de pagamento e obrigatorio")
        MetodoPagamento metodo
) {
}
