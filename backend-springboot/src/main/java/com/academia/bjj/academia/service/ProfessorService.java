package com.academia.bjj.academia.service;

import com.academia.bjj.academia.dto.ProfessorRequest;
import com.academia.bjj.academia.dto.ProfessorResponse;
import com.academia.bjj.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ProfessorService {
    ProfessorResponse criar(ProfessorRequest request);

    ProfessorResponse atualizar(Long id, ProfessorRequest request);

    ProfessorResponse buscar(Long id);

    PageResponse<ProfessorResponse> listar(String nome, Pageable pageable);

    void remover(Long id);
}
