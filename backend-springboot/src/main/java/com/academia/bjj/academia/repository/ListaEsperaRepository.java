package com.academia.bjj.academia.repository;

import com.academia.bjj.academia.model.ListaEspera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {

    List<ListaEspera> findByTurmaIdOrderByPosicaoAsc(Long turmaId);

    boolean existsByTurmaIdAndAlunoId(Long turmaId, Long alunoId);

    long countByTurmaId(Long turmaId);
}
