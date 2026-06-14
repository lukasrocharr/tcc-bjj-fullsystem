package com.academia.bjj.ecommerce.shipping;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Frete mock por regiao do CEP (diretriz 7). Frete gratis acima de R$ 300.
 * Regra simples baseada no primeiro digito do CEP (regiao dos Correios).
 */
@Service
public class FixedRateShippingService implements ShippingService {

    private static final BigDecimal FRETE_GRATIS_ACIMA = new BigDecimal("300.00");

    @Override
    public BigDecimal calcularFrete(String cep, BigDecimal subtotal) {
        if (subtotal != null && subtotal.compareTo(FRETE_GRATIS_ACIMA) >= 0) {
            return BigDecimal.ZERO;
        }
        String digitos = cep == null ? "" : cep.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            return new BigDecimal("29.90");
        }
        char regiao = digitos.charAt(0);
        // Sudeste (0,1,2) mais barato; demais regioes mais caro.
        return switch (regiao) {
            case '0', '1', '2' -> new BigDecimal("19.90");
            case '3', '4' -> new BigDecimal("29.90");
            default -> new BigDecimal("39.90");
        };
    }
}
