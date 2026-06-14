package com.academia.bjj.ecommerce.controller;

import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.ecommerce.dto.CategoriaRequest;
import com.academia.bjj.ecommerce.dto.CategoriaResponse;
import com.academia.bjj.ecommerce.dto.MovimentoEstoqueRequest;
import com.academia.bjj.ecommerce.dto.ProdutoRequest;
import com.academia.bjj.ecommerce.dto.ProdutoResponse;
import com.academia.bjj.ecommerce.dto.VariacaoRequest;
import com.academia.bjj.ecommerce.dto.VariacaoResponse;
import com.academia.bjj.ecommerce.service.CategoriaService;
import com.academia.bjj.ecommerce.service.ProdutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Catalogo da loja (RF-011 a RF-017, RF-033 a RF-035). Leitura publica;
 * escrita (produtos, variacoes, estoque) apenas ADMIN.
 */
@RestController
@RequestMapping("/api/v1/loja")
@Tag(name = "Loja - Catalogo", description = "Categorias, produtos, variacoes e estoque")
public class LojaCatalogoController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";

    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;

    public LojaCatalogoController(CategoriaService categoriaService, ProdutoService produtoService) {
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
    }

    // ----- Categorias -----
    @GetMapping("/categorias")
    public List<CategoriaResponse> listarCategorias() {
        return categoriaService.listar();
    }

    @PostMapping("/categorias")
    @PreAuthorize(ADMIN)
    public ResponseEntity<CategoriaResponse> criarCategoria(@Valid @RequestBody CategoriaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(req));
    }

    @PutMapping("/categorias/{id}")
    @PreAuthorize(ADMIN)
    public CategoriaResponse atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequest req) {
        return categoriaService.atualizar(id, req);
    }

    @DeleteMapping("/categorias/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> removerCategoria(@PathVariable Long id) {
        categoriaService.remover(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Produtos -----
    @GetMapping("/produtos")
    public PageResponse<ProdutoResponse> listarProdutos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "true") boolean apenasAtivos,
            @PageableDefault(size = 12) Pageable pageable) {
        return produtoService.listar(categoriaId, busca, apenasAtivos, pageable);
    }

    @GetMapping("/produtos/{id}")
    public ProdutoResponse buscarProduto(@PathVariable Long id) {
        return produtoService.buscar(id);
    }

    @PostMapping("/produtos")
    @PreAuthorize(ADMIN)
    public ResponseEntity<ProdutoResponse> criarProduto(@Valid @RequestBody ProdutoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(req));
    }

    @PutMapping("/produtos/{id}")
    @PreAuthorize(ADMIN)
    public ProdutoResponse atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequest req) {
        return produtoService.atualizar(id, req);
    }

    @DeleteMapping("/produtos/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> removerProduto(@PathVariable Long id) {
        produtoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Variacoes / estoque -----
    @PostMapping("/produtos/{produtoId}/variacoes")
    @PreAuthorize(ADMIN)
    public ResponseEntity<VariacaoResponse> adicionarVariacao(
            @PathVariable Long produtoId, @Valid @RequestBody VariacaoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.adicionarVariacao(produtoId, req));
    }

    @PutMapping("/variacoes/{id}")
    @PreAuthorize(ADMIN)
    public VariacaoResponse atualizarVariacao(@PathVariable Long id, @Valid @RequestBody VariacaoRequest req) {
        return produtoService.atualizarVariacao(id, req);
    }

    @DeleteMapping("/variacoes/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> removerVariacao(@PathVariable Long id) {
        produtoService.removerVariacao(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/variacoes/{id}/estoque")
    @PreAuthorize(ADMIN)
    public VariacaoResponse movimentarEstoque(@PathVariable Long id, @Valid @RequestBody MovimentoEstoqueRequest req) {
        return produtoService.movimentarEstoque(id, req);
    }
}
