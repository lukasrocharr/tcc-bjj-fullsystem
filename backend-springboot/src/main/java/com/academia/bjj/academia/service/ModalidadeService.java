package com.academia.bjj.academia.service;

import com.academia.bjj.academia.dto.ModalidadeRequest;
import com.academia.bjj.academia.dto.ModalidadeResponse;
import com.academia.bjj.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ModalidadeService {
    ModalidadeResponse criar(ModalidadeRequest request);

    ModalidadeResponse atualizar(Long id, ModalidadeRequest request);

    ModalidadeResponse buscar(Long id);

    PageResponse<ModalidadeResponse> listar(Boolean ativo, Pageable pageable);

    void remover(Long id);
}
