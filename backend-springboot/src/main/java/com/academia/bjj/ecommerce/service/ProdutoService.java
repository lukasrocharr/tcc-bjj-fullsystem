package com.academia.bjj.ecommerce.service;

import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.ecommerce.dto.MovimentoEstoqueRequest;
import com.academia.bjj.ecommerce.dto.ProdutoRequest;
import com.academia.bjj.ecommerce.dto.ProdutoResponse;
import com.academia.bjj.ecommerce.dto.VariacaoRequest;
import com.academia.bjj.ecommerce.dto.VariacaoResponse;
import org.springframework.data.domain.Pageable;

public interface ProdutoService {

    ProdutoResponse criar(ProdutoRequest request);

    ProdutoResponse atualizar(Long id, ProdutoRequest request);

    ProdutoResponse buscar(Long id);

    PageResponse<ProdutoResponse> listar(Long categoriaId, String busca, boolean apenasAtivos, Pageable pageable);

    void remover(Long id);

    VariacaoResponse adicionarVariacao(Long produtoId, VariacaoRequest request);

    VariacaoResponse atualizarVariacao(Long variacaoId, VariacaoRequest request);

    void removerVariacao(Long variacaoId);

    /** Entrada/saida de estoque com motivo (RF-035). */
    VariacaoResponse movimentarEstoque(Long variacaoId, MovimentoEstoqueRequest request);
}
