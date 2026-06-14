package com.academia.bjj.auditoria.dto;

import java.time.OffsetDateTime;

public record AuditoriaResponse(
        Long id,
        Long usuarioId,
        String usuarioEmail,
        String metodo,
        String caminho,
        int status,
        OffsetDateTime criadoEm
) {
}
