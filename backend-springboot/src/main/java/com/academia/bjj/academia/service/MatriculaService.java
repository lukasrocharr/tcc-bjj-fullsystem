package com.academia.bjj.academia.service;

import com.academia.bjj.academia.dto.ListaEsperaResponse;
import com.academia.bjj.academia.dto.MatriculaRequest;
import com.academia.bjj.academia.dto.MatriculaResponse;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MatriculaService {
    MatriculaResponse criar(MatriculaRequest request);

    MatriculaResponse buscar(Long id);

    PageResponse<MatriculaResponse> listar(Long alunoId, StatusMatricula status, Pageable pageable);

    MatriculaResponse alterarStatus(Long id, StatusMatricula novoStatus);

    /** Adiciona um aluno a lista de espera de uma turma lotada (RF-047). */
    ListaEsperaResponse entrarListaEspera(Long turmaId, Long alunoId);

    List<ListaEsperaResponse> listaEspera(Long turmaId);
}
