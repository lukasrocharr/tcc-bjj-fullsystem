package com.academia.bjj.auth.repository;

import com.academia.bjj.auth.model.PapelNome;
import com.academia.bjj.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByPapeis_Nome(PapelNome nome);
}
