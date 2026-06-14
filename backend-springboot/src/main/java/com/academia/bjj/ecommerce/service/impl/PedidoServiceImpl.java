package com.academia.bjj.ecommerce.service.impl;

import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.ecommerce.dto.AtualizarStatusPedidoRequest;
import com.academia.bjj.ecommerce.dto.CheckoutRequest;
import com.academia.bjj.ecommerce.dto.EnderecoRequest;
import com.academia.bjj.ecommerce.dto.PedidoResponse;
import com.academia.bjj.ecommerce.mapper.EcommerceMapper;
import com.academia.bjj.ecommerce.model.Carrinho;
import com.academia.bjj.ecommerce.model.Cupom;
import com.academia.bjj.ecommerce.model.EnderecoEntrega;
import com.academia.bjj.ecommerce.model.ItemCarrinho;
import com.academia.bjj.ecommerce.model.ItemPedido;
import com.academia.bjj.ecommerce.model.Pedido;
import com.academia.bjj.ecommerce.model.StatusPedido;
import com.academia.bjj.ecommerce.model.TipoCupom;
import com.academia.bjj.ecommerce.model.VariacaoProduto;
import com.academia.bjj.ecommerce.repository.CupomRepository;
import com.academia.bjj.ecommerce.repository.PedidoRepository;
import com.academia.bjj.ecommerce.repository.VariacaoProdutoRepository;
import com.academia.bjj.ecommerce.service.CarrinhoService;
import com.academia.bjj.ecommerce.service.PedidoService;
import com.academia.bjj.ecommerce.shipping.ShippingService;
import com.academia.bjj.financeiro.gateway.PaymentGatewayService;
import com.academia.bjj.notificacao.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class PedidoServiceImpl implements PedidoService {

    private static final DateTimeFormatter NUM = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final PedidoRepository repository;
    private final CarrinhoService carrinhoService;
    private final VariacaoProdutoRepository variacaoRepository;
    private final CupomRepository cupomRepository;
    private final ShippingService shippingService;
    private final PaymentGatewayService gateway;
    private final EcommerceMapper mapper;
    private final NotificationService notificationService;
    private final Clock clock;

    public PedidoServiceImpl(PedidoRepository repository,
                             CarrinhoService carrinhoService,
                             VariacaoProdutoRepository variacaoRepository,
                             CupomRepository cupomRepository,
                             ShippingService shippingService,
                             PaymentGatewayService gateway,
                             EcommerceMapper mapper,
                             NotificationService notificationService,
                             Clock clock) {
        this.repository = repository;
        this.carrinhoService = carrinhoService;
        this.variacaoRepository = variacaoRepository;
        this.cupomRepository = cupomRepository;
        this.shippingService = shippingService;
        this.gateway = gateway;
        this.mapper = mapper;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @Override
    @Transactional
    public PedidoResponse checkout(Long usuarioId, String sessionId, CheckoutRequest request) {
        Carrinho carrinho = carrinhoService.carrinhoParaCheckout(usuarioId, sessionId);

        Pedido pedido = new Pedido();
        pedido.setNumero(gerarNumero());
        pedido.setUsuario(carrinho.getUsuario());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setEndereco(snapshotEndereco(request.endereco()));

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemCarrinho ic : carrinho.getItens()) {
            VariacaoProduto v = ic.getVariacao();
            if (ic.getQuantidade() > v.getEstoque()) {
                throw new BusinessException("Estoque insuficiente para " + v.getSku());
            }
            BigDecimal precoUnit = v.getPrecoEfetivo();
            BigDecimal sub = precoUnit.multiply(BigDecimal.valueOf(ic.getQuantidade()));

            ItemPedido item = new ItemPedido();
            item.setVariacao(v);
            item.setNomeProduto(v.getProduto().getNome());
            item.setSku(v.getSku());
            item.setPrecoUnitario(precoUnit);
            item.setQuantidade(ic.getQuantidade());
            item.setSubtotal(sub);
            pedido.addItem(item);

            subtotal = subtotal.add(sub);
        }

        BigDecimal desconto = calcularDesconto(request.cupomCodigo(), subtotal, pedido);
        BigDecimal frete = shippingService.calcularFrete(request.endereco().cep(), subtotal);
        BigDecimal total = subtotal.add(frete).subtract(desconto);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        pedido.setSubtotal(subtotal);
        pedido.setFrete(frete);
        pedido.setDesconto(desconto);
        pedido.setTotal(total);
        pedido = repository.save(pedido);

        // Cobranca (gateway mock aprova). Estoque so baixa na confirmacao.
        PaymentGatewayService.GatewayResult resultado =
                gateway.cobrar(request.metodo(), total, pedido.getNumero());

        Pedido confirmado = aplicarPagamento(pedido, resultado.gatewayId(), resultado.aprovado());

        carrinhoService.limpar(carrinho);
        return mapper.toResponse(confirmado);
    }

    @Override
    @Transactional
    public PedidoResponse confirmarPagamento(String numeroPedido, String gatewayId, boolean aprovado) {
        Pedido pedido = repository.findByNumero(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + numeroPedido));
        return mapper.toResponse(aplicarPagamento(pedido, gatewayId, aprovado));
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id) {
        return mapper.toResponse(obter(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PedidoResponse> listar(StatusPedido status, Pageable pageable) {
        return PageResponse.from(repository.buscar(status, pageable).map(mapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PedidoResponse> meusPedidos(Long usuarioId, Pageable pageable) {
        return PageResponse.from(repository.findByUsuarioIdOrderByIdDesc(usuarioId, pageable).map(mapper::toResponse));
    }

    @Override
    @Transactional
    public PedidoResponse atualizarStatus(Long id, AtualizarStatusPedidoRequest request) {
        Pedido pedido = obter(id);
        if (request.status() == StatusPedido.CANCELADO) {
            return cancelar(id);
        }
        pedido.setStatus(request.status());
        if (request.rastreio() != null) {
            pedido.setRastreio(request.rastreio());
        }
        return mapper.toResponse(repository.save(pedido));
    }

    @Override
    @Transactional
    public PedidoResponse cancelar(Long id) {
        Pedido pedido = obter(id);
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            return mapper.toResponse(pedido);
        }
        // Devolve estoque se ja havia sido baixado (pagamento confirmado).
        if (estoqueFoiBaixado(pedido.getStatus())) {
            for (ItemPedido item : pedido.getItens()) {
                VariacaoProduto v = item.getVariacao();
                if (v != null) {
                    v.setEstoque(v.getEstoque() + item.getQuantidade());
                    variacaoRepository.save(v);
                }
            }
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return mapper.toResponse(repository.save(pedido));
    }

    // ---------------- helpers ----------------

    private Pedido obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));
    }

    private Pedido aplicarPagamento(Pedido pedido, String gatewayId, boolean aprovado) {
        pedido.setGatewayId(gatewayId);
        if (!aprovado || pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            return repository.save(pedido); // idempotente / pagamento recusado
        }
        // Baixa de estoque na confirmacao do pagamento (RF-030).
        for (ItemPedido item : pedido.getItens()) {
            VariacaoProduto v = item.getVariacao();
            if (v == null) {
                throw new BusinessException("Variacao do item nao esta mais disponivel");
            }
            if (item.getQuantidade() > v.getEstoque()) {
                throw new BusinessException("Estoque insuficiente para " + v.getSku() + " na confirmacao");
            }
            v.setEstoque(v.getEstoque() - item.getQuantidade());
            variacaoRepository.save(v);
        }
        pedido.setStatus(StatusPedido.PAGO);
        Pedido salvo = repository.save(pedido);

        if (salvo.getUsuario() != null) {
            notificationService.enviarEmail(salvo.getUsuario().getEmail(),
                    "Pedido confirmado",
                    "Seu pedido " + salvo.getNumero() + " foi pago. Total: R$ " + salvo.getTotal() + ".");
        }
        return salvo;
    }

    private boolean estoqueFoiBaixado(StatusPedido status) {
        return status == StatusPedido.PAGO
                || status == StatusPedido.ENVIADO
                || status == StatusPedido.ENTREGUE;
    }

    private BigDecimal calcularDesconto(String codigo, BigDecimal subtotal, Pedido pedido) {
        if (codigo == null || codigo.isBlank()) {
            return BigDecimal.ZERO;
        }
        Cupom cupom = cupomRepository.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new BusinessException("Cupom invalido: " + codigo));
        if (!cupom.isVigente(LocalDate.now(clock))) {
            throw new BusinessException("Cupom expirado ou inativo");
        }
        if (subtotal.compareTo(cupom.getMinSubtotal()) < 0) {
            throw new BusinessException("Subtotal minimo para o cupom: R$ " + cupom.getMinSubtotal());
        }
        pedido.setCupomCodigo(cupom.getCodigo());

        BigDecimal desconto = cupom.getTipo() == TipoCupom.PERCENTUAL
                ? subtotal.multiply(cupom.getValor()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : cupom.getValor();
        return desconto.min(subtotal);
    }

    private EnderecoEntrega snapshotEndereco(EnderecoRequest r) {
        EnderecoEntrega e = new EnderecoEntrega();
        e.setCep(r.cep());
        e.setLogradouro(r.logradouro());
        e.setNumero(r.numero());
        e.setComplemento(r.complemento());
        e.setBairro(r.bairro());
        e.setCidade(r.cidade());
        e.setUf(r.uf());
        return e;
    }

    private String gerarNumero() {
        return "PED-" + LocalDate.now(clock).format(NUM) + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
