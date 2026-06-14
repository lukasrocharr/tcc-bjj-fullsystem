package com.academia.bjj.notificacao;

/**
 * Abstracao de envio de notificacoes/e-mails (diretriz 7 e RF-102).
 * Implementacao padrao escreve no console; trocar por SMTP via config.
 */
public interface NotificationService {

    void enviarEmail(String destinatario, String assunto, String corpo);
}
