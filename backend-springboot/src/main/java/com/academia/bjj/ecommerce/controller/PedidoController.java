package com.academia.bjj.ecommerce.controller;

import com.academia.bjj.auth.security.JwtAuthenticationFilter.AuthenticatedUser;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.ecommerce.dto.AtualizarStatusPedidoRequest;
import com.academia.bjj.ecommerce.dto.CheckoutRequest;
import com.academia.bjj.ecommerce.dto.PedidoResponse;
import com.academia.bjj.ecommerce.dto.WebhookPagamentoRequest;
import com.academia.bjj.ecommerce.model.StatusPedido;
import com.academia.bjj.ecommerce.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Checkout, webhook de pagamento, area do cliente e gestao de pedidos
 * (RF-029 a RF-040).
 */
@RestController
@RequestMapping("/api/v1/loja")
@Tag(name = "Loja - Pedidos", description = "Checkout, pagamento, pedidos do cliente e gestao")
public class PedidoController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Finaliza a compra a partir do carrinho")
    public ResponseEntity<PedidoResponse> checkout(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @Valid @RequestBody CheckoutRequest req) {
        Long usuarioId = user != null ? user.id() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(service.checkout(usuarioId, sessionId, req));
    }

    @PostMapping("/webhook/pagamento")
    @Operation(summary = "Webhook do gateway: confirma/recusa pagamento e baixa estoque")
    public PedidoResponse webhook(@Valid @RequestBody WebhookPagamentoRequest req) {
        return service.confirmarPagamento(req.numeroPedido(), req.gatewayId(), req.aprovado());
    }

    @GetMapping("/meus-pedidos")
    @Operation(summary = "Historico de pedidos do usuario autenticado")
    public PageResponse<PedidoResponse> meusPedidos(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.meusPedidos(user.id(), pageable);
    }

    @GetMapping("/admin/pedidos")
    @PreAuthorize(ADMIN)
    public PageResponse<PedidoResponse> listar(
            @RequestParam(required = false) StatusPedido status,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.listar(status, pageable);
    }

    @GetMapping("/admin/pedidos/{id}")
    @PreAuthorize(ADMIN)
    public PedidoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PatchMapping("/admin/pedidos/{id}/status")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Atualiza status/rastreio; CANCELADO devolve estoque")
    public PedidoResponse atualizarStatus(@PathVariable Long id,
                                          @Valid @RequestBody AtualizarStatusPedidoRequest req) {
        return service.atualizarStatus(id, req);
    }
}
