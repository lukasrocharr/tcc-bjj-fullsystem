package com.academia.bjj.financeiro.gateway;

import com.academia.bjj.financeiro.model.MetodoPagamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementacao mock/sandbox: sempre aprova e gera um id de transacao
 * (diretriz 7). Ativa por padrao (payment.provider=mock).
 */
@Service
@ConditionalOnProperty(name = "payment.provider", havingValue = "mock", matchIfMissing = true)
public class MockPaymentGatewayService implements PaymentGatewayService {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGatewayService.class);

    @Override
    public GatewayResult cobrar(MetodoPagamento metodo, BigDecimal valor, String referencia) {
        String gatewayId = "MOCK-" + UUID.randomUUID();
        log.info("[GATEWAY MOCK] Cobranca aprovada: metodo={}, valor={}, ref={}, id={}",
                metodo, valor, referencia, gatewayId);
        return new GatewayResult(true, gatewayId);
    }
}
