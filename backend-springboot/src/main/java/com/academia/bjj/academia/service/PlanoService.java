package com.academia.bjj.academia.service;

import com.academia.bjj.academia.dto.PlanoRequest;
import com.academia.bjj.academia.dto.PlanoResponse;
import com.academia.bjj.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface PlanoService {
    PlanoResponse criar(PlanoRequest request);

    PlanoResponse atualizar(Long id, PlanoRequest request);

    PlanoResponse buscar(Long id);

    PageResponse<PlanoResponse> listar(Boolean ativo, Pageable pageable);

    void remover(Long id);
}
