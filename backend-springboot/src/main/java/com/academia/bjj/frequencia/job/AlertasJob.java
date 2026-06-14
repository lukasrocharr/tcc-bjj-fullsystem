package com.academia.bjj.frequencia.job;

import com.academia.bjj.frequencia.service.CheckInService;
import com.academia.bjj.graduacao.service.GraduacaoService;
import com.academia.bjj.notificacao.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job diario que dispara alertas administrativos de baixa frequencia (RF-061)
 * e de elegibilidade a graduacao (RF-072). Roda as 08:00 todos os dias.
 */
@Component
public class AlertasJob {

    private static final Logger log = LoggerFactory.getLogger(AlertasJob.class);

    private final CheckInService checkInService;
    private final GraduacaoService graduacaoService;
    private final NotificationService notificationService;

    public AlertasJob(CheckInService checkInService,
                      GraduacaoService graduacaoService,
                      NotificationService notificationService) {
        this.checkInService = checkInService;
        this.graduacaoService = graduacaoService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${app.frequencia.alertas-cron:0 0 8 * * *}")
    public void dispararAlertas() {
        var baixaFrequencia = checkInService.alertasBaixaFrequencia();
        var elegiveis = graduacaoService.elegiveis();

        if (baixaFrequencia.isEmpty() && elegiveis.isEmpty()) {
            return;
        }

        StringBuilder corpo = new StringBuilder("Relatorio diario de alertas da academia:\n\n");
        corpo.append("Baixa frequencia (").append(baixaFrequencia.size()).append("):\n");
        baixaFrequencia.forEach(a -> corpo.append(" - ").append(a.nome()).append("\n"));
        corpo.append("\nElegiveis a graduacao (").append(elegiveis.size()).append("):\n");
        elegiveis.forEach(a -> corpo.append(" - ").append(a.nome()).append("\n"));

        log.info("Alertas diarios: {} baixa frequencia, {} elegiveis",
                baixaFrequencia.size(), elegiveis.size());
        notificationService.enviarEmail("admin@bjj.local", "Alertas diarios da academia", corpo.toString());
    }
}
