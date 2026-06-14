package com.academia.bjj.financeiro.service;

import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.financeiro.dto.GerarMensalidadesResponse;
import com.academia.bjj.financeiro.dto.MensalidadeResponse;
import com.academia.bjj.financeiro.dto.PagamentoResponse;
import com.academia.bjj.financeiro.dto.RelatorioFinanceiroResponse;
import com.academia.bjj.financeiro.model.MetodoPagamento;
import com.academia.bjj.financeiro.model.StatusMensalidade;
import org.springframework.data.domain.Pageable;

public interface MensalidadeService {

    /** Gera 1 mensalidade por matricula ativa na competencia, sem duplicar (RF-074, UC-04). */
    GerarMensalidadesResponse gerar(int ano, int mes);

    PageResponse<MensalidadeResponse> listar(Long alunoId, StatusMensalidade status, Pageable pageable);

    MensalidadeResponse buscar(Long id);

    /** Registra pagamento via gateway/manual; aprova -> marca PAGA (RF-076). */
    PagamentoResponse pagar(Long mensalidadeId, MetodoPagamento metodo);

    MensalidadeResponse cancelar(Long id);

    /** Marca vencidas como ATRASADA e aplica multa/juros (RF-077). Retorna quantas processou. */
    int atualizarAtrasadas();

    /** Bloqueio por inadimplencia: atraso alem do limite configurado (RF-081). */
    boolean alunoBloqueado(Long alunoId);

    byte[] recibo(Long mensalidadeId);

    /** Relatorio financeiro consolidado (RF-082). ano/mes opcionais. */
    RelatorioFinanceiroResponse relatorio(Integer ano, Integer mes);
}
