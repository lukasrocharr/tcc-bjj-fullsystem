package com.academia.bjj.academia.service;

import com.academia.bjj.academia.dto.AlunoRequest;
import com.academia.bjj.academia.dto.AlunoResponse;
import com.academia.bjj.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AlunoService {
    AlunoResponse criar(AlunoRequest request);

    AlunoResponse atualizar(Long id, AlunoRequest request);

    AlunoResponse buscar(Long id);

    /** Aluno vinculado ao usuario autenticado (portal do aluno). */
    AlunoResponse meuPerfil(Long usuarioId);

    PageResponse<AlunoResponse> listar(String nome, Pageable pageable);

    void remover(Long id);
}
