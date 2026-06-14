package com.academia.bjj.academia.repository;

import com.academia.bjj.academia.model.Modalidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModalidadeRepository extends JpaRepository<Modalidade, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Page<Modalidade> findByAtivo(boolean ativo, Pageable pageable);
}
