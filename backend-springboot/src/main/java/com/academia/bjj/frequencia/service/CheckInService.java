package com.academia.bjj.frequencia.service;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.frequencia.dto.ChamadaRequest;
import com.academia.bjj.frequencia.dto.CheckInRequest;
import com.academia.bjj.frequencia.dto.CheckInResponse;
import com.academia.bjj.frequencia.dto.FrequenciaResponse;

import java.util.List;

public interface CheckInService {

    /** Check-in self-service do aluno, com validacao de matricula e janela (RF-056, RF-057). */
    CheckInResponse registrar(CheckInRequest request);

    /** Chamada em lote pelo professor (RF-058). Ignora alunos ja presentes no dia. */
    List<CheckInResponse> registrarChamada(ChamadaRequest request);

    /** Indicadores de frequencia do aluno (RF-059, RF-060). */
    FrequenciaResponse frequenciaDoAluno(Long alunoId);

    List<CheckInResponse> historicoDoAluno(Long alunoId);

    /** Alunos ativos sem check-in alem do limite configurado (RF-061). */
    List<AlunoRef> alertasBaixaFrequencia();
}
