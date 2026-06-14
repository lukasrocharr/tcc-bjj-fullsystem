package com.academia.bjj.financeiro.service.impl;

import com.academia.bjj.academia.model.Matricula;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.academia.repository.MatriculaRepository;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.config.AppProperties;
import com.academia.bjj.financeiro.dto.GerarMensalidadesResponse;
import com.academia.bjj.financeiro.dto.MensalidadeResponse;
import com.academia.bjj.financeiro.dto.PagamentoResponse;
import com.academia.bjj.financeiro.dto.RelatorioFinanceiroResponse;
import com.academia.bjj.financeiro.gateway.PaymentGatewayService;
import com.academia.bjj.financeiro.mapper.FinanceiroMapper;
import com.academia.bjj.financeiro.model.Mensalidade;
import com.academia.bjj.financeiro.model.MetodoPagamento;
import com.academia.bjj.financeiro.model.Pagamento;
import com.academia.bjj.financeiro.model.StatusMensalidade;
import com.academia.bjj.financeiro.model.StatusPagamento;
import com.academia.bjj.financeiro.repository.MensalidadeRepository;
import com.academia.bjj.financeiro.repository.PagamentoRepository;
import com.academia.bjj.financeiro.service.MensalidadeService;
import com.academia.bjj.financeiro.service.ReciboPdfService;
import com.academia.bjj.notificacao.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MensalidadeServiceImpl implements MensalidadeService {

    private final MensalidadeRepository repository;
    private final PagamentoRepository pagamentoRepository;
    private final MatriculaRepository matriculaRepository;
    private final PaymentGatewayService gateway;
    private final FinanceiroMapper mapper;
    private final ReciboPdfService reciboPdfService;
    private final NotificationService notificationService;
    private final AppProperties props;
    private final Clock clock;

    public MensalidadeServiceImpl(MensalidadeRepository repository,
                                  PagamentoRepository pagamentoRepository,
                                  MatriculaRepository matriculaRepository,
                                  PaymentGatewayService gateway,
                                  FinanceiroMapper mapper,
                                  ReciboPdfService reciboPdfService,
                                  NotificationService notificationService,
                                  AppProperties props,
                                  Clock clock) {
        this.repository = repository;
        this.pagamentoRepository = pagamentoRepository;
        this.matriculaRepository = matriculaRepository;
        this.gateway = gateway;
        this.mapper = mapper;
        this.reciboPdfService = reciboPdfService;
        this.notificationService = notificationService;
        this.props = props;
        this.clock = clock;
    }

    @Override
    @Transactional
    public GerarMensalidadesResponse gerar(int ano, int mes) {
        LocalDate vencimento = vencimentoDe(ano, mes);
        int geradas = 0;
        int ignoradas = 0;

        for (Matricula matricula : matriculaRepository.findByStatus(StatusMatricula.ATIVA)) {
            if (repository.existsByMatriculaIdAndAnoAndMes(matricula.getId(), ano, mes)) {
                ignoradas++;
                continue;
            }
            Mensalidade m = new Mensalidade();
            m.setMatricula(matricula);
            m.setAno(ano);
            m.setMes(mes);
            m.setValor(matricula.getPlano().getValor());
            m.setDataVencimento(vencimento);
            m.setStatus(StatusMensalidade.PENDENTE);
            repository.save(m);
            geradas++;
        }
        return new GerarMensalidadesResponse(ano, mes, geradas, ignoradas);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MensalidadeResponse> listar(Long alunoId, StatusMensalidade status, Pageable pageable) {
        return PageResponse.from(repository.buscar(alunoId, status, pageable).map(mapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public MensalidadeResponse buscar(Long id) {
        return mapper.toResponse(obter(id));
    }

    @Override
    @Transactional
    public PagamentoResponse pagar(Long mensalidadeId, MetodoPagamento metodo) {
        Mensalidade m = obter(mensalidadeId);
        if (m.getStatus() == StatusMensalidade.PAGA) {
            throw new BusinessException("Mensalidade ja esta paga");
        }
        if (m.getStatus() == StatusMensalidade.CANCELADA) {
            throw new BusinessException("Mensalidade cancelada nao pode ser paga");
        }

        // Garante encargos atualizados se vencida antes de cobrar.
        aplicarEncargosSeVencida(m, hoje());

        BigDecimal total = m.getValorTotal();
        OffsetDateTime agora = OffsetDateTime.now(clock);

        PaymentGatewayService.GatewayResult resultado =
                gateway.cobrar(metodo, total, "mensalidade-" + m.getId());

        StatusPagamento statusPag = resultado.aprovado() ? StatusPagamento.APROVADO : StatusPagamento.RECUSADO;
        Pagamento pagamento = new Pagamento(m, total, metodo, statusPag, resultado.gatewayId(), agora);
        pagamento = pagamentoRepository.save(pagamento);

        if (resultado.aprovado()) {
            m.setStatus(StatusMensalidade.PAGA);
            m.setValorPago(total);
            m.setDataPagamento(hoje());
            repository.save(m);
            notificationService.enviarEmail(m.getMatricula().getAluno().getEmail(),
                    "Pagamento confirmado",
                    "Recebemos o pagamento da mensalidade " + m.getMes() + "/" + m.getAno()
                            + " no valor de R$ " + total + ".");
        }
        return mapper.toResponse(pagamento);
    }

    @Override
    @Transactional
    public MensalidadeResponse cancelar(Long id) {
        Mensalidade m = obter(id);
        if (m.getStatus() == StatusMensalidade.PAGA) {
            throw new BusinessException("Mensalidade paga nao pode ser cancelada");
        }
        m.setStatus(StatusMensalidade.CANCELADA);
        return mapper.toResponse(repository.save(m));
    }

    @Override
    @Transactional
    public int atualizarAtrasadas() {
        LocalDate hoje = hoje();
        List<Mensalidade> vencidas = repository.findByStatusAndDataVencimentoBefore(
                StatusMensalidade.PENDENTE, hoje);
        List<Mensalidade> jaAtrasadas = repository.findByStatusAndDataVencimentoBefore(
                StatusMensalidade.ATRASADA, hoje);

        int processadas = 0;
        for (Mensalidade m : vencidas) {
            boolean novaAtrasada = m.getStatus() != StatusMensalidade.ATRASADA;
            aplicarEncargos(m, hoje);
            repository.save(m);
            processadas++;
            if (novaAtrasada) {
                notificationService.enviarEmail(m.getMatricula().getAluno().getEmail(),
                        "Mensalidade em atraso",
                        "Sua mensalidade " + m.getMes() + "/" + m.getAno()
                                + " esta em atraso. Valor atualizado: R$ " + m.getValorTotal() + ".");
            }
        }
        for (Mensalidade m : jaAtrasadas) {
            aplicarEncargos(m, hoje); // atualiza juros do dia
            repository.save(m);
            processadas++;
        }
        return processadas;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean alunoBloqueado(Long alunoId) {
        LocalDate corte = hoje().minusDays(props.getFinanceiro().getBloqueioDias());
        return repository.existsByMatricula_Aluno_IdAndStatusAndDataVencimentoBefore(
                alunoId, StatusMensalidade.ATRASADA, corte);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] recibo(Long mensalidadeId) {
        return reciboPdfService.gerar(obter(mensalidadeId));
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioFinanceiroResponse relatorio(Integer ano, Integer mes) {
        List<Mensalidade> base = (ano != null && mes != null)
                ? repository.findByAnoAndMes(ano, mes)
                : repository.findAll();

        BigDecimal recebido = BigDecimal.ZERO;
        BigDecimal pendente = BigDecimal.ZERO;
        BigDecimal atrasado = BigDecimal.ZERO;
        long qtdPagas = 0;
        long qtdPendentes = 0;
        long qtdAtrasadas = 0;

        for (Mensalidade m : base) {
            switch (m.getStatus()) {
                case PAGA -> {
                    recebido = recebido.add(m.getValorPago() != null ? m.getValorPago() : m.getValorTotal());
                    qtdPagas++;
                }
                case PENDENTE -> {
                    pendente = pendente.add(m.getValorTotal());
                    qtdPendentes++;
                }
                case ATRASADA -> {
                    atrasado = atrasado.add(m.getValorTotal());
                    qtdAtrasadas++;
                }
                case CANCELADA -> { /* nao entra no relatorio */ }
            }
        }

        BigDecimal totalLoja = BigDecimal.ZERO; // Fase 5 (e-commerce)
        BigDecimal totalGeral = recebido.add(totalLoja);
        return new RelatorioFinanceiroResponse(ano, mes, recebido, pendente, atrasado,
                qtdPagas, qtdPendentes, qtdAtrasadas, totalLoja, totalGeral);
    }

    // ---------------- helpers ----------------

    private Mensalidade obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensalidade nao encontrada: " + id));
    }

    private LocalDate hoje() {
        return LocalDate.now(clock);
    }

    private LocalDate vencimentoDe(int ano, int mes) {
        YearMonth ym = YearMonth.of(ano, mes);
        int dia = Math.min(props.getFinanceiro().getDiaVencimento(), ym.lengthOfMonth());
        return ym.atDay(dia);
    }

    private void aplicarEncargosSeVencida(Mensalidade m, LocalDate hoje) {
        if (m.getStatus() != StatusMensalidade.PAGA
                && m.getStatus() != StatusMensalidade.CANCELADA
                && m.getDataVencimento().isBefore(hoje)) {
            aplicarEncargos(m, hoje);
        }
    }

    private void aplicarEncargos(Mensalidade m, LocalDate hoje) {
        long diasAtraso = Math.max(0, ChronoUnit.DAYS.between(m.getDataVencimento(), hoje));
        BigDecimal valor = m.getValor();
        BigDecimal multa = valor
                .multiply(BigDecimal.valueOf(props.getFinanceiro().getMultaPercentual()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal juros = valor
                .multiply(BigDecimal.valueOf(props.getFinanceiro().getJurosDiaPercentual()))
                .multiply(BigDecimal.valueOf(diasAtraso))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        m.setMulta(multa);
        m.setJuros(juros);
        m.setStatus(StatusMensalidade.ATRASADA);
    }
}
