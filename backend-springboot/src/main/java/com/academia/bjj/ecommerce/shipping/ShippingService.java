package com.academia.bjj.ecommerce.shipping;

import java.math.BigDecimal;

/**
 * Abstracao de calculo de frete (diretriz 7). Implementacao padrao usa uma
 * tabela simples por regiao do CEP; trocavel por integracao real.
 */
public interface ShippingService {

    BigDecimal calcularFrete(String cep, BigDecimal subtotal);
}
