package com.academia.bjj.academia.repository;

import com.academia.bjj.academia.model.Matricula;
import com.academia.bjj.academia.model.StatusMatricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByAlunoId(Long alunoId);

    List<Matricula> findByStatus(StatusMatricula status);

    long countByStatus(StatusMatricula status);

    long countByCreatedAtGreaterThanEqual(java.time.OffsetDateTime inicio);

    @Query("""
            select count(m) from Matricula m join m.turmas t
            where t.id = :turmaId and m.status = :status
            """)
    long contarPorTurmaEStatus(@Param("turmaId") Long turmaId,
                               @Param("status") StatusMatricula status);

    boolean existsByAlunoIdAndStatus(Long alunoId, StatusMatricula status);

    @Query("""
            select (count(m) > 0) from Matricula m join m.turmas t
            where m.aluno.id = :alunoId and t.id = :turmaId and m.status = :status
            """)
    boolean existeMatriculaComAlunoTurmaEStatus(@Param("alunoId") Long alunoId,
                                                @Param("turmaId") Long turmaId,
                                                @Param("status") StatusMatricula status);

    @Query("""
            select m from Matricula m
            where (:alunoId is null or m.aluno.id = :alunoId)
              and (:status is null or m.status = :status)
            """)
    Page<Matricula> buscar(@Param("alunoId") Long alunoId,
                           @Param("status") StatusMatricula status,
                           Pageable pageable);
}
