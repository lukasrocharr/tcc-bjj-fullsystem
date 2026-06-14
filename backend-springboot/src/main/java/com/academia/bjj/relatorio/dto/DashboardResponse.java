package com.academia.bjj.relatorio.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Indicadores do dashboard administrativo (RF-091 a RF-093).
 */
public record DashboardResponse(
        long alunosAtivos,
        long novasMatriculasMes,
        long turmas,
        long produtos,
        long pedidosPendentes,
        long riscoEvasao,
        BigDecimal receitaMensalidadesMes,
        BigDecimal receitaLojaMes,
        BigDecimal receitaTotalMes,
        BigDecimal inadimplenciaValor,
        long inadimplenciaQtd,
        List<PontoSerie> serieReceita
) {
    /** Ponto da serie de evolucao de receita (para graficos). */
    public record PontoSerie(String competencia, BigDecimal valor) {
    }
}
