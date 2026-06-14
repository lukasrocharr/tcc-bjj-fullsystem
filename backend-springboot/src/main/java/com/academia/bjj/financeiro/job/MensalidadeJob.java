package com.academia.bjj.financeiro.job;

import com.academia.bjj.financeiro.service.MensalidadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Jobs financeiros (RF-074, RF-077):
 *  - geracao mensal de mensalidades (dia 1, 02:00);
 *  - atualizacao diaria de mensalidades atrasadas (03:00).
 */
@Component
public class MensalidadeJob {

    private static final Logger log = LoggerFactory.getLogger(MensalidadeJob.class);

    private final MensalidadeService service;

    public MensalidadeJob(MensalidadeService service) {
        this.service = service;
    }

    @Scheduled(cron = "${app.financeiro.geracao-cron:0 0 2 1 * *}")
    public void gerarMensalidadesDoMes() {
        LocalDate hoje = LocalDate.now();
        var resultado = service.gerar(hoje.getYear(), hoje.getMonthValue());
        log.info("Geracao mensal: {} geradas, {} ignoradas ({}/{})",
                resultado.geradas(), resultado.ignoradas(), resultado.mes(), resultado.ano());
    }

    @Scheduled(cron = "${app.financeiro.atraso-cron:0 0 3 * * *}")
    public void atualizarAtrasadas() {
        int n = service.atualizarAtrasadas();
        if (n > 0) {
            log.info("Mensalidades atrasadas reprocessadas: {}", n);
        }
    }
}
