package com.academia.bjj.academia.service;

import com.academia.bjj.academia.dto.TurmaRequest;
import com.academia.bjj.academia.dto.TurmaResponse;
import com.academia.bjj.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TurmaService {
    TurmaResponse criar(TurmaRequest request);

    TurmaResponse atualizar(Long id, TurmaRequest request);

    TurmaResponse buscar(Long id);

    PageResponse<TurmaResponse> listar(Long modalidadeId, Boolean ativo, Pageable pageable);

    /** Grade de horarios completa, ordenada por dia e horario (RF-051). */
    List<TurmaResponse> grade();

    void remover(Long id);
}
