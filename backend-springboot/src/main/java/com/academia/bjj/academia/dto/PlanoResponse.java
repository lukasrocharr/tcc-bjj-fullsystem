package com.academia.bjj.academia.dto;

import com.academia.bjj.academia.model.Periodicidade;

import java.math.BigDecimal;
import java.util.List;

public record PlanoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal valor,
        Periodicidade periodicidade,
        int aulasPorSemana,
        boolean ativo,
        List<ModalidadeRef> modalidades
) {
}
