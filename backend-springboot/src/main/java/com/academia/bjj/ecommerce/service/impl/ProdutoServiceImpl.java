package com.academia.bjj.ecommerce.service.impl;

import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ConflictException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.ecommerce.dto.MovimentoEstoqueRequest;
import com.academia.bjj.ecommerce.dto.ProdutoRequest;
import com.academia.bjj.ecommerce.dto.ProdutoResponse;
import com.academia.bjj.ecommerce.dto.VariacaoRequest;
import com.academia.bjj.ecommerce.dto.VariacaoResponse;
import com.academia.bjj.ecommerce.mapper.EcommerceMapper;
import com.academia.bjj.ecommerce.model.Categoria;
import com.academia.bjj.ecommerce.model.Produto;
import com.academia.bjj.ecommerce.model.VariacaoProduto;
import com.academia.bjj.ecommerce.repository.CategoriaRepository;
import com.academia.bjj.ecommerce.repository.ProdutoRepository;
import com.academia.bjj.ecommerce.repository.VariacaoProdutoRepository;
import com.academia.bjj.ecommerce.service.ProdutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private static final Logger log = LoggerFactory.getLogger(ProdutoServiceImpl.class);

    private final ProdutoRepository repository;
    private final VariacaoProdutoRepository variacaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EcommerceMapper mapper;

    public ProdutoServiceImpl(ProdutoRepository repository,
                              VariacaoProdutoRepository variacaoRepository,
                              CategoriaRepository categoriaRepository,
                              EcommerceMapper mapper) {
        this.repository = repository;
        this.variacaoRepository = variacaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto p = new Produto();
        aplicar(p, request);
        return toResponse(repository.save(p));
    }

    @Override
    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto p = obter(id);
        aplicar(p, request);
        return toResponse(repository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        return toResponse(obter(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProdutoResponse> listar(Long categoriaId, String busca,
                                                boolean apenasAtivos, Pageable pageable) {
        var page = repository.buscar(categoriaId, busca, apenasAtivos, pageable).map(this::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    @Override
    @Transactional
    public VariacaoResponse adicionarVariacao(Long produtoId, VariacaoRequest request) {
        Produto produto = obter(produtoId);
        if (variacaoRepository.existsBySku(request.sku())) {
            throw new ConflictException("Ja existe uma variacao com este SKU");
        }
        VariacaoProduto v = new VariacaoProduto();
        v.setProduto(produto);
        aplicarVariacao(v, request);
        return mapper.toResponse(variacaoRepository.save(v));
    }

    @Override
    @Transactional
    public VariacaoResponse atualizarVariacao(Long variacaoId, VariacaoRequest request) {
        VariacaoProduto v = obterVariacao(variacaoId);
        if (!v.getSku().equalsIgnoreCase(request.sku()) && variacaoRepository.existsBySku(request.sku())) {
            throw new ConflictException("Ja existe uma variacao com este SKU");
        }
        aplicarVariacao(v, request);
        return mapper.toResponse(variacaoRepository.save(v));
    }

    @Override
    @Transactional
    public void removerVariacao(Long variacaoId) {
        variacaoRepository.delete(obterVariacao(variacaoId));
    }

    @Override
    @Transactional
    public VariacaoResponse movimentarEstoque(Long variacaoId, MovimentoEstoqueRequest request) {
        VariacaoProduto v = obterVariacao(variacaoId);
        int novo = request.tipo() == MovimentoEstoqueRequest.Tipo.ENTRADA
                ? v.getEstoque() + request.quantidade()
                : v.getEstoque() - request.quantidade();
        if (novo < 0) {
            throw new BusinessException("Estoque insuficiente para a saida solicitada");
        }
        v.setEstoque(novo);
        log.info("Estoque SKU {} {} {} -> {} (motivo: {})", v.getSku(), request.tipo(),
                request.quantidade(), novo, request.motivo());
        return mapper.toResponse(variacaoRepository.save(v));
    }

    // ---------------- helpers ----------------

    private Produto obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + id));
    }

    private VariacaoProduto obterVariacao(Long id) {
        return variacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variacao nao encontrada: " + id));
    }

    private void aplicar(Produto p, ProdutoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new BusinessException("Categoria inexistente: " + request.categoriaId()));
        p.setNome(request.nome().trim());
        p.setDescricao(request.descricao());
        p.setCategoria(categoria);
        p.setPreco(request.preco());
        if (request.ativo() != null) {
            p.setAtivo(request.ativo());
        }
        p.setImagens(request.imagens() != null ? new ArrayList<>(request.imagens()) : new ArrayList<>());
    }

    private void aplicarVariacao(VariacaoProduto v, VariacaoRequest request) {
        v.setSku(request.sku().trim());
        v.setTamanho(request.tamanho());
        v.setCor(request.cor());
        v.setPrecoAdicional(request.precoAdicional() != null ? request.precoAdicional() : BigDecimal.ZERO);
        v.setEstoque(request.estoque());
    }

    private ProdutoResponse toResponse(Produto produto) {
        List<VariacaoResponse> variacoes = variacaoRepository.findByProdutoId(produto.getId())
                .stream().map(mapper::toResponse).toList();
        return mapper.toResponse(produto, variacoes);
    }
}
