package com.academia.bjj.graduacao.repository;

import com.academia.bjj.graduacao.model.Graduacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GraduacaoRepository extends JpaRepository<Graduacao, Long> {

    List<Graduacao> findByAlunoIdOrderByDataDescIdDesc(Long alunoId);

    /** Ultima graduacao do aluno = faixa atual derivada (RF-072). */
    Optional<Graduacao> findFirstByAlunoIdOrderByDataDescIdDesc(Long alunoId);
}
