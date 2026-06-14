package com.academia.bjj.notificacao.dto;

import com.academia.bjj.auth.model.PapelNome;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Comunicado em massa (RF-104). Se {@code papel} for nulo, envia para todos.
 */
public record ComunicadoRequest(
        @NotBlank(message = "O titulo e obrigatorio")
        @Size(max = 140)
        String titulo,

        @NotBlank(message = "A mensagem e obrigatoria")
        @Size(max = 2000)
        String mensagem,

        PapelNome papel,

        boolean enviarEmail
) {
}
