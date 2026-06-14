package com.academia.bjj.notificacao.dto;

import java.time.OffsetDateTime;

public record NotificacaoResponse(
        Long id,
        String titulo,
        String mensagem,
        boolean lida,
        OffsetDateTime criadoEm
) {
}
