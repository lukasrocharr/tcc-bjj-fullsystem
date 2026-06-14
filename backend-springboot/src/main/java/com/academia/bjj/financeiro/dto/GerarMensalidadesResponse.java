package com.academia.bjj.financeiro.dto;

public record GerarMensalidadesResponse(
        int ano,
        int mes,
        int geradas,
        int ignoradas
) {
}
