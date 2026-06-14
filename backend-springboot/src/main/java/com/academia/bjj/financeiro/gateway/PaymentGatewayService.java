package com.academia.bjj.financeiro.gateway;

import com.academia.bjj.financeiro.model.MetodoPagamento;

import java.math.BigDecimal;

/**
 * Abstracao de gateway de pagamento (diretriz 7 do prompt mestre). A
 * implementacao padrao e um mock/sandbox; trocavel via configuracao.
 */
public interface PaymentGatewayService {

    /** Resultado de uma cobranca. */
    record GatewayResult(boolean aprovado, String gatewayId) {
    }

    GatewayResult cobrar(MetodoPagamento metodo, BigDecimal valor, String referencia);
}
