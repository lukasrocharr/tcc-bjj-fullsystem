package com.academia.bjj.financeiro.repository;

import com.academia.bjj.financeiro.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByMensalidadeIdOrderByDataHoraDesc(Long mensalidadeId);
}
