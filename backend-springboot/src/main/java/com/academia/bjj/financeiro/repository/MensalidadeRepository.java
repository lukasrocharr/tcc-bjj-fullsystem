package com.academia.bjj.financeiro.repository;

import com.academia.bjj.financeiro.model.Mensalidade;
import com.academia.bjj.financeiro.model.StatusMensalidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    boolean existsByMatriculaIdAndAnoAndMes(Long matriculaId, int ano, int mes);

    List<Mensalidade> findByStatusAndDataVencimentoBefore(StatusMensalidade status, LocalDate data);

    List<Mensalidade> findByAnoAndMes(int ano, int mes);

    /** Suporte ao bloqueio por inadimplencia (RF-081). */
    boolean existsByMatricula_Aluno_IdAndStatusAndDataVencimentoBefore(
            Long alunoId, StatusMensalidade status, LocalDate corte);

    @Query("""
            select m from Mensalidade m
            where (:alunoId is null or m.matricula.aluno.id = :alunoId)
              and (:status is null or m.status = :status)
            order by m.ano desc, m.mes desc
            """)
    Page<Mensalidade> buscar(@Param("alunoId") Long alunoId,
                             @Param("status") StatusMensalidade status,
                             Pageable pageable);
}
