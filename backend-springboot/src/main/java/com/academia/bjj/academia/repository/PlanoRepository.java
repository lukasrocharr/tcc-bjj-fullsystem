package com.academia.bjj.academia.repository;

import com.academia.bjj.academia.model.Plano;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoRepository extends JpaRepository<Plano, Long> {

    Page<Plano> findByAtivo(boolean ativo, Pageable pageable);
}
