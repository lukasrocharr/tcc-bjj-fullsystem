package com.academia.bjj.ecommerce.repository;

import com.academia.bjj.ecommerce.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    Optional<Carrinho> findByUsuarioId(Long usuarioId);

    Optional<Carrinho> findBySessionId(String sessionId);
}
