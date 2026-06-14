package com.academia.bjj.graduacao.dto;

import com.academia.bjj.graduacao.model.CategoriaFaixa;

public record FaixaResponse(
        Long id,
        String nome,
        CategoriaFaixa categoria,
        int ordem,
        int grausMax,
        boolean ativo
) {
}
