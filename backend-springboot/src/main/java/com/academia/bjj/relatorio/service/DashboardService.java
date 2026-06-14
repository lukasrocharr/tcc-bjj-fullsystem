package com.academia.bjj.relatorio.service;

import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.academia.repository.MatriculaRepository;
import com.academia.bjj.academia.repository.TurmaRepository;
import com.academia.bjj.ecommerce.model.StatusPedido;
import com.academia.bjj.ecommerce.repository.PedidoRepository;
import com.academia.bjj.ecommerce.repository.ProdutoRepository;
import com.academia.bjj.financeiro.dto.RelatorioFinanceiroResponse;
import com.academia.bjj.financeiro.service.MensalidadeService;
import com.academia.bjj.frequencia.service.CheckInService;
import com.academia.bjj.relatorio.dto.DashboardResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Agrega indicadores de todos os modulos para o dashboard (RF-091 a RF-093).
 */
@Service
public class DashboardService {

    private static final DateTimeFormatter COMP = DateTimeFormatter.ofPattern("MM/yyyy");

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;
    private final TurmaRepository turmaRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final MensalidadeService mensalidadeService;
    private final CheckInService checkInService;
    private final Clock clock;

    public DashboardService(AlunoRepository alunoRepository,
                            MatriculaRepository matriculaRepository,
                            TurmaRepository turmaRepository,
                            ProdutoRepository produtoRepository,
                            PedidoRepository pedidoRepository,
                            MensalidadeService mensalidadeService,
                            CheckInService checkInService,
                            Clock clock) {
        this.alunoRepository = alunoRepository;
        this.matriculaRepository = matriculaRepository;
        this.turmaRepository = turmaRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.mensalidadeService = mensalidadeService;
        this.checkInService = checkInService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public DashboardResponse gerar() {
        LocalDate hoje = LocalDate.now(clock);
        YearMonth atual = YearMonth.from(hoje);

        RelatorioFinanceiroResponse rel = mensalidadeService.relatorio(atual.getYear(), atual.getMonthValue());
        BigDecimal receitaLoja = somarLojaNoMes(atual);
        BigDecimal receitaTotal = rel.totalRecebido().add(receitaLoja);

        long novasMatriculas = matriculaRepository.countByCreatedAtGreaterThanEqual(
                atual.atDay(1).atStartOfDay(zona()).toOffsetDateTime());

        return new DashboardResponse(
                alunoRepository.countByAtivoTrue(),
                novasMatriculas,
                turmaRepository.count(),
                produtoRepository.count(),
                pedidoRepository.countByStatus(StatusPedido.AGUARDANDO_PAGAMENTO),
                checkInService.alertasBaixaFrequencia().size(),
                rel.totalRecebido(),
                receitaLoja,
                receitaTotal,
                rel.totalAtrasado(),
                rel.qtdAtrasadas(),
                serieReceita(atual));
    }

    private List<DashboardResponse.PontoSerie> serieReceita(YearMonth ate) {
        List<DashboardResponse.PontoSerie> serie = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = ate.minusMonths(i);
            BigDecimal mensalidades = mensalidadeService.relatorio(ym.getYear(), ym.getMonthValue()).totalRecebido();
            BigDecimal loja = somarLojaNoMes(ym);
            serie.add(new DashboardResponse.PontoSerie(ym.format(COMP), mensalidades.add(loja)));
        }
        return serie;
    }

    private BigDecimal somarLojaNoMes(YearMonth ym) {
        var inicio = ym.atDay(1).atStartOfDay(zona()).toOffsetDateTime();
        var fim = ym.plusMonths(1).atDay(1).atStartOfDay(zona()).toOffsetDateTime();
        BigDecimal soma = pedidoRepository.somarPagosNoPeriodo(inicio, fim);
        return soma != null ? soma : BigDecimal.ZERO;
    }

    private ZoneId zona() {
        return clock.getZone();
    }
}
