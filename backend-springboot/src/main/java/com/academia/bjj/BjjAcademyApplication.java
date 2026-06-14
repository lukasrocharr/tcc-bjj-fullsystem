package com.academia.bjj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicacao principal do Sistema de Gestao de Academia de BJJ + E-commerce.
 *
 * Habilita agendamento (geracao de mensalidades, alertas) e processamento
 * assincrono (envio de notificacoes), conforme diretrizes 8 e 9 do prompt mestre.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BjjAcademyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BjjAcademyApplication.class, args);
    }
}
