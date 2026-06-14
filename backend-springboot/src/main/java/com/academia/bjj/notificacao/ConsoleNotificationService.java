package com.academia.bjj.notificacao;

import com.academia.bjj.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementacao padrao (provider=console): registra o e-mail no log.
 * Envio assincrono para nao bloquear a requisicao (diretriz 9).
 */
@Service
public class ConsoleNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationService.class);

    private final AppProperties props;

    public ConsoleNotificationService(AppProperties props) {
        this.props = props;
    }

    @Override
    @Async
    public void enviarEmail(String destinatario, String assunto, String corpo) {
        log.info("""

                ====================== [E-MAIL SIMULADO] ======================
                De:       {}
                Para:     {}
                Assunto:  {}
                ---------------------------------------------------------------
                {}
                ===============================================================
                """, props.getNotification().getFrom(), destinatario, assunto, corpo);
    }
}
