package com.academia.bjj.auth.repository;

import com.academia.bjj.auth.model.RefreshToken;
import com.academia.bjj.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("update RefreshToken r set r.revogado = true where r.usuario = :usuario and r.revogado = false")
    void revogarTodosDoUsuario(@Param("usuario") Usuario usuario);
}
