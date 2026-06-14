package com.academia.bjj.ecommerce.service;

import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.ecommerce.dto.AtualizarStatusPedidoRequest;
import com.academia.bjj.ecommerce.dto.CheckoutRequest;
import com.academia.bjj.ecommerce.dto.PedidoResponse;
import com.academia.bjj.ecommerce.model.StatusPedido;
import org.springframework.data.domain.Pageable;

public interface PedidoService {

    /** Finaliza a compra a partir do carrinho (RF-029). */
    PedidoResponse checkout(Long usuarioId, String sessionId, CheckoutRequest request);

    /** Confirma (ou recusa) o pagamento; aprovado -> PAGO + baixa de estoque (RF-030). */
    PedidoResponse confirmarPagamento(String numeroPedido, String gatewayId, boolean aprovado);

    PedidoResponse buscar(Long id);

    PageResponse<PedidoResponse> listar(StatusPedido status, Pageable pageable);

    PageResponse<PedidoResponse> meusPedidos(Long usuarioId, Pageable pageable);

    /** Admin: muda status / rastreio (RF-031). */
    PedidoResponse atualizarStatus(Long id, AtualizarStatusPedidoRequest request);

    /** Admin: cancela pedido; devolve estoque se ja havia sido baixado (RF-032). */
    PedidoResponse cancelar(Long id);
}
