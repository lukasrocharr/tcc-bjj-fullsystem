package com.academia.bjj.financeiro;

import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.model.Matricula;
import com.academia.bjj.academia.model.Plano;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.academia.repository.MatriculaRepository;
import com.academia.bjj.config.AppProperties;
import com.academia.bjj.financeiro.dto.GerarMensalidadesResponse;
import com.academia.bjj.financeiro.dto.PagamentoResponse;
import com.academia.bjj.financeiro.gateway.PaymentGatewayService;
import com.academia.bjj.financeiro.mapper.FinanceiroMapper;
import com.academia.bjj.financeiro.model.Mensalidade;
import com.academia.bjj.financeiro.model.MetodoPagamento;
import com.academia.bjj.financeiro.model.Pagamento;
import com.academia.bjj.financeiro.model.StatusMensalidade;
import com.academia.bjj.financeiro.repository.MensalidadeRepository;
import com.academia.bjj.financeiro.repository.PagamentoRepository;
import com.academia.bjj.financeiro.service.ReciboPdfService;
import com.academia.bjj.financeiro.service.impl.MensalidadeServiceImpl;
import com.academia.bjj.notificacao.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regras criticas do financeiro (criterios de aceitacao do SRS):
 *  - geracao gera exatamente 1 mensalidade por matricula ativa por competencia,
 *    sem duplicar (RF-074);
 *  - pagamento aprovado marca a mensalidade como PAGA (RF-076).
 */
@ExtendWith(MockitoExtension.class)
class MensalidadeServiceImplTest {

    @Mock MensalidadeRepository repository;
    @Mock PagamentoRepository pagamentoRepository;
    @Mock MatriculaRepository matriculaRepository;
    @Mock PaymentGatewayService gateway;
    @Mock FinanceiroMapper mapper;
    @Mock ReciboPdfService reciboPdfService;
    @Mock NotificationService notificationService;

    AppProperties props = new AppProperties();
    Clock clock = Clock.fixed(LocalDate.of(2026, 6, 13).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());
    MensalidadeServiceImpl service;

    Matricula ativa;

    @BeforeEach
    void setup() {
        service = new MensalidadeServiceImpl(repository, pagamentoRepository, matriculaRepository,
                gateway, mapper, reciboPdfService, notificationService, props, clock);

        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno");
        aluno.setEmail("aluno@bjj.local");

        Plano plano = new Plano();
        plano.setId(1L);
        plano.setValor(new BigDecimal("150.00"));

        ativa = new Matricula();
        ativa.setId(100L);
        ativa.setAluno(aluno);
        ativa.setPlano(plano);
        ativa.setStatus(StatusMatricula.ATIVA);
    }

    @Test
    void gerar_criaUmaPorMatriculaAtiva_semDuplicar() {
        when(matriculaRepository.findByStatus(StatusMatricula.ATIVA)).thenReturn(List.of(ativa));
        // Ainda nao existe mensalidade nesta competencia.
        when(repository.existsByMatriculaIdAndAnoAndMes(100L, 2026, 6)).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GerarMensalidadesResponse r1 = service.gerar(2026, 6);
        assertThat(r1.geradas()).isEqualTo(1);
        assertThat(r1.ignoradas()).isZero();

        // Segunda execucao na mesma competencia: ja existe -> ignora (idempotente).
        when(repository.existsByMatriculaIdAndAnoAndMes(100L, 2026, 6)).thenReturn(true);
        GerarMensalidadesResponse r2 = service.gerar(2026, 6);
        assertThat(r2.geradas()).isZero();
        assertThat(r2.ignoradas()).isEqualTo(1);

        // save chamado apenas 1 vez no total (apenas na primeira geracao).
        verify(repository, times(1)).save(any(Mensalidade.class));
    }

    @Test
    void pagar_aprovado_marcaComoPaga() {
        Mensalidade m = new Mensalidade();
        m.setMatricula(ativa);
        m.setAno(2026);
        m.setMes(6);
        m.setValor(new BigDecimal("150.00"));
        m.setDataVencimento(LocalDate.of(2026, 6, 10));
        m.setStatus(StatusMensalidade.PENDENTE);

        when(repository.findById(5L)).thenReturn(Optional.of(m));
        when(gateway.cobrar(eq(MetodoPagamento.PIX), any(), any()))
                .thenReturn(new PaymentGatewayService.GatewayResult(true, "MOCK-123"));
        when(pagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        lenient().when(mapper.toResponse(any(Pagamento.class))).thenReturn(
                new PagamentoResponse(1L, 5L, new BigDecimal("153.30"), MetodoPagamento.PIX,
                        com.academia.bjj.financeiro.model.StatusPagamento.APROVADO, "MOCK-123", null));

        service.pagar(5L, MetodoPagamento.PIX);

        assertThat(m.getStatus()).isEqualTo(StatusMensalidade.PAGA);
        assertThat(m.getDataPagamento()).isEqualTo(LocalDate.of(2026, 6, 13));
        assertThat(m.getValorPago()).isNotNull();
        verify(notificationService).enviarEmail(eq("aluno@bjj.local"), any(), any());
    }

    @Test
    void atualizarAtrasadas_aplicaMultaEJuros_eMarcaAtrasada() {
        Mensalidade vencida = new Mensalidade();
        vencida.setMatricula(ativa);
        vencida.setAno(2026);
        vencida.setMes(5);
        vencida.setValor(new BigDecimal("100.00"));
        vencida.setDataVencimento(LocalDate.of(2026, 6, 3)); // 10 dias de atraso ate 13/06
        vencida.setStatus(StatusMensalidade.PENDENTE);

        when(repository.findByStatusAndDataVencimentoBefore(eq(StatusMensalidade.PENDENTE), any()))
                .thenReturn(List.of(vencida));
        when(repository.findByStatusAndDataVencimentoBefore(eq(StatusMensalidade.ATRASADA), any()))
                .thenReturn(List.of());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        int processadas = service.atualizarAtrasadas();

        assertThat(processadas).isEqualTo(1);
        assertThat(vencida.getStatus()).isEqualTo(StatusMensalidade.ATRASADA);
        // multa = 2% de 100 = 2.00 ; juros = 0.033% * 100 * 10 dias = 0.33
        assertThat(vencida.getMulta()).isEqualByComparingTo("2.00");
        assertThat(vencida.getJuros()).isEqualByComparingTo("0.33");
    }
}
