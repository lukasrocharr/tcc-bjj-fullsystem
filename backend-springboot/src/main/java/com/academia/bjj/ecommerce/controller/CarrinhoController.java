package com.academia.bjj.ecommerce.controller;

import com.academia.bjj.auth.security.JwtAuthenticationFilter.AuthenticatedUser;
import com.academia.bjj.ecommerce.dto.AddItemRequest;
import com.academia.bjj.ecommerce.dto.CarrinhoResponse;
import com.academia.bjj.ecommerce.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Carrinho (RF-018 a RF-021). Usuario autenticado usa o proprio carrinho;
 * visitante usa o header {@code X-Session-Id}.
 */
@RestController
@RequestMapping("/api/v1/loja/carrinho")
@Tag(name = "Loja - Carrinho", description = "Carrinho do usuario/visitante")
public class CarrinhoController {

    private final CarrinhoService service;

    public CarrinhoController(CarrinhoService service) {
        this.service = service;
    }

    @GetMapping
    public CarrinhoResponse ver(@AuthenticationPrincipal AuthenticatedUser user,
                                @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        return service.ver(idDe(user), sessionId);
    }

    @PostMapping("/itens")
    @Operation(summary = "Adiciona um item (valida estoque)")
    public CarrinhoResponse adicionar(@AuthenticationPrincipal AuthenticatedUser user,
                                      @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
                                      @Valid @RequestBody AddItemRequest req) {
        return service.adicionar(idDe(user), sessionId, req);
    }

    @PutMapping("/itens/{itemId}")
    public CarrinhoResponse atualizar(@AuthenticationPrincipal AuthenticatedUser user,
                                      @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
                                      @PathVariable Long itemId,
                                      @RequestParam int quantidade) {
        return service.atualizarQuantidade(idDe(user), sessionId, itemId, quantidade);
    }

    @DeleteMapping("/itens/{itemId}")
    public CarrinhoResponse remover(@AuthenticationPrincipal AuthenticatedUser user,
                                    @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
                                    @PathVariable Long itemId) {
        return service.remover(idDe(user), sessionId, itemId);
    }

    private Long idDe(AuthenticatedUser user) {
        return user != null ? user.id() : null;
    }
}
