package com.academia.bjj.ecommerce.service;

import com.academia.bjj.ecommerce.dto.AddItemRequest;
import com.academia.bjj.ecommerce.dto.CarrinhoResponse;
import com.academia.bjj.ecommerce.model.Carrinho;

public interface CarrinhoService {

    CarrinhoResponse ver(Long usuarioId, String sessionId);

    CarrinhoResponse adicionar(Long usuarioId, String sessionId, AddItemRequest request);

    CarrinhoResponse atualizarQuantidade(Long usuarioId, String sessionId, Long itemId, int quantidade);

    CarrinhoResponse remover(Long usuarioId, String sessionId, Long itemId);

    /** Uso interno do checkout: carrinho existente e nao vazio, ou erro. */
    Carrinho carrinhoParaCheckout(Long usuarioId, String sessionId);

    void limpar(Carrinho carrinho);
}
