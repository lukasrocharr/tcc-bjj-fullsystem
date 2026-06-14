package com.academia.bjj.graduacao.repository;

import com.academia.bjj.graduacao.model.CategoriaFaixa;
import com.academia.bjj.graduacao.model.Faixa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaixaRepository extends JpaRepository<Faixa, Long> {

    List<Faixa> findByCategoriaOrderByOrdemAsc(CategoriaFaixa categoria);

    List<Faixa> findAllByOrderByCategoriaAscOrdemAsc();
}
