package com.academia.bjj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Fornece um {@link Clock} injetavel para logica sensivel a tempo
 * (janela de check-in), permitindo substituicao em testes.
 */
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
