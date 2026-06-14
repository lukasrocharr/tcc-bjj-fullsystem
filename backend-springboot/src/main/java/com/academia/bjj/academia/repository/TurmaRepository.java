package com.academia.bjj.academia.repository;

import com.academia.bjj.academia.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    @Query("""
            select t from Turma t
            where (:modalidadeId is null or t.modalidade.id = :modalidadeId)
              and (:ativo is null or t.ativo = :ativo)
            """)
    Page<Turma> buscar(@Param("modalidadeId") Long modalidadeId,
                       @Param("ativo") Boolean ativo,
                       Pageable pageable);

    List<Turma> findByProfessorIdOrderByDiaSemanaAscHoraInicioAsc(Long professorId);

    List<Turma> findAllByOrderByDiaSemanaAscHoraInicioAsc();
}
