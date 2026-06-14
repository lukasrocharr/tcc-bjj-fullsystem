package com.academia.bjj.auth.repository;

import com.academia.bjj.auth.model.Papel;
import com.academia.bjj.auth.model.PapelNome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PapelRepository extends JpaRepository<Papel, Long> {

    Optional<Papel> findByNome(PapelNome nome);
}
