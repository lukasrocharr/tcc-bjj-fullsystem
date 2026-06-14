package com.academia.bjj.ecommerce.service.impl;

import com.academia.bjj.auth.model.Usuario;
import com.academia.bjj.auth.repository.UsuarioRepository;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.ecommerce.dto.AddItemRequest;
import com.academia.bjj.ecommerce.dto.CarrinhoResponse;
import com.academia.bjj.ecommerce.dto.ItemCarrinhoResponse;
import com.academia.bjj.ecommerce.model.Carrinho;
import com.academia.bjj.ecommerce.model.ItemCarrinho;
import com.academia.bjj.ecommerce.model.VariacaoProduto;
import com.academia.bjj.ecommerce.repository.CarrinhoRepository;
import com.academia.bjj.ecommerce.repository.VariacaoProdutoRepository;
import com.academia.bjj.ecommerce.service.CarrinhoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CarrinhoServiceImpl implements CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final VariacaoProdutoRepository variacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarrinhoServiceImpl(CarrinhoRepository carrinhoRepository,
                               VariacaoProdutoRepository variacaoRepository,
                               UsuarioRepository usuarioRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.variacaoRepository = variacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public CarrinhoResponse ver(Long usuarioId, String sessionId) {
        return toResponse(obterOuCriar(usuarioId, sessionId));
    }

    @Override
    @Transactional
    public CarrinhoResponse adicionar(Long usuarioId, String sessionId, AddItemRequest request) {
        Carrinho carrinho = obterOuCriar(usuarioId, sessionId);
        VariacaoProduto variacao = variacaoRepository.findById(request.variacaoId())
                .orElseThrow(() -> new BusinessException("Variacao inexistente: " + request.variacaoId()));

        ItemCarrinho existente = carrinho.getItens().stream()
                .filter(i -> i.getVariacao().getId().equals(variacao.getId()))
                .findFirst().orElse(null);

        int novaQtd = (existente != null ? existente.getQuantidade() : 0) + request.quantidade();
        validarEstoque(variacao, novaQtd);

        if (existente != null) {
            existente.setQuantidade(novaQtd);
        } else {
            ItemCarrinho item = new ItemCarrinho();
            item.setCarrinho(carrinho);
            item.setVariacao(variacao);
            item.setQuantidade(request.quantidade());
            carrinho.getItens().add(item);
        }
        return toResponse(carrinhoRepository.save(carrinho));
    }

    @Override
    @Transactional
    public CarrinhoResponse atualizarQuantidade(Long usuarioId, String sessionId, Long itemId, int quantidade) {
        Carrinho carrinho = obterOuCriar(usuarioId, sessionId);
        ItemCarrinho item = carrinho.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item nao encontrado no carrinho: " + itemId));

        if (quantidade <= 0) {
            carrinho.getItens().remove(item);
        } else {
            validarEstoque(item.getVariacao(), quantidade);
            item.setQuantidade(quantidade);
        }
        return toResponse(carrinhoRepository.save(carrinho));
    }

    @Override
    @Transactional
    public CarrinhoResponse remover(Long usuarioId, String sessionId, Long itemId) {
        Carrinho carrinho = obterOuCriar(usuarioId, sessionId);
        carrinho.getItens().removeIf(i -> i.getId().equals(itemId));
        return toResponse(carrinhoRepository.save(carrinho));
    }

    @Override
    @Transactional
    public Carrinho carrinhoParaCheckout(Long usuarioId, String sessionId) {
        Carrinho carrinho = localizar(usuarioId, sessionId)
                .orElseThrow(() -> new BusinessException("Carrinho vazio"));
        if (carrinho.getItens().isEmpty()) {
            throw new BusinessException("Carrinho vazio");
        }
        return carrinho;
    }

    @Override
    @Transactional
    public void limpar(Carrinho carrinho) {
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);
    }

    // ---------------- helpers ----------------

    private java.util.Optional<Carrinho> localizar(Long usuarioId, String sessionId) {
        if (usuarioId != null) {
            return carrinhoRepository.findByUsuarioId(usuarioId);
        }
        if (sessionId != null && !sessionId.isBlank()) {
            return carrinhoRepository.findBySessionId(sessionId);
        }
        throw new BusinessException("Informe um usuario autenticado ou um identificador de sessao");
    }

    private Carrinho obterOuCriar(Long usuarioId, String sessionId) {
        return localizar(usuarioId, sessionId).orElseGet(() -> {
            Carrinho c = new Carrinho();
            if (usuarioId != null) {
                Usuario u = usuarioRepository.findById(usuarioId)
                        .orElseThrow(() -> new BusinessException("Usuario inexistente: " + usuarioId));
                c.setUsuario(u);
            } else {
                c.setSessionId(sessionId);
            }
            return carrinhoRepository.save(c);
        });
    }

    private void validarEstoque(VariacaoProduto variacao, int quantidade) {
        if (quantidade > variacao.getEstoque()) {
            throw new BusinessException("Estoque insuficiente para " + variacao.getSku()
                    + " (disponivel: " + variacao.getEstoque() + ")");
        }
    }

    private CarrinhoResponse toResponse(Carrinho carrinho) {
        List<ItemCarrinhoResponse> itens = carrinho.getItens().stream()
                .map(this::toItemResponse)
                .toList();
        int totalItens = itens.stream().mapToInt(ItemCarrinhoResponse::quantidade).sum();
        BigDecimal subtotal = itens.stream()
                .map(ItemCarrinhoResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CarrinhoResponse(carrinho.getId(), itens, totalItens, subtotal);
    }

    private ItemCarrinhoResponse toItemResponse(ItemCarrinho item) {
        VariacaoProduto v = item.getVariacao();
        BigDecimal precoUnit = v.getPrecoEfetivo();
        BigDecimal subtotal = precoUnit.multiply(BigDecimal.valueOf(item.getQuantidade()));
        return new ItemCarrinhoResponse(
                item.getId(), v.getId(), v.getSku(), v.getProduto().getNome(),
                v.getTamanho(), v.getCor(), precoUnit, item.getQuantidade(), subtotal, v.getEstoque());
    }
}
