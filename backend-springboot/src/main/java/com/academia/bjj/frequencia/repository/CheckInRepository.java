package com.academia.bjj.frequencia.repository;

import com.academia.bjj.frequencia.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    boolean existsByAlunoIdAndTurmaIdAndData(Long alunoId, Long turmaId, LocalDate data);

    long countByAlunoId(Long alunoId);

    long countByAlunoIdAndDataBetween(Long alunoId, LocalDate inicio, LocalDate fim);

    List<CheckIn> findByAlunoIdOrderByDataDesc(Long alunoId);

    List<CheckIn> findByTurmaIdAndDataOrderByDataHoraAsc(Long turmaId, LocalDate data);

    /** Alunos ativos sem nenhum check-in a partir da data de corte (RF-061). */
    @Query("""
            select a.id from Aluno a
            where a.ativo = true
              and not exists (
                  select 1 from CheckIn c where c.aluno = a and c.data >= :corte
              )
            """)
    List<Long> idsAlunosAtivosSemCheckInDesde(@Param("corte") LocalDate corte);
}
