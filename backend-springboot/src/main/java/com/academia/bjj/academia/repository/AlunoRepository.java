package com.academia.bjj.academia.repository;

import com.academia.bjj.academia.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    boolean existsByEmailIgnoreCase(String email);

    List<Aluno> findByAtivoTrue();

    long countByAtivoTrue();

    Optional<Aluno> findByUsuarioId(Long usuarioId);

    Page<Aluno> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
