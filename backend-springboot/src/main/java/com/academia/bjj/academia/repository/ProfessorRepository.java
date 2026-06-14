package com.academia.bjj.academia.repository;

import com.academia.bjj.academia.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Professor> findByUsuarioId(Long usuarioId);

    Page<Professor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
