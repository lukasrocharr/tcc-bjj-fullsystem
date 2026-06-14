package com.academia.bjj.graduacao.service;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.graduacao.dto.FaixaAtualResponse;
import com.academia.bjj.graduacao.dto.FaixaResponse;
import com.academia.bjj.graduacao.dto.GraduacaoRequest;
import com.academia.bjj.graduacao.dto.GraduacaoResponse;

import java.util.List;

public interface GraduacaoService {

    /** Registra promocao: grava no historico e atualiza a faixa atual do aluno (RF-070, RF-071). */
    GraduacaoResponse registrar(GraduacaoRequest request);

    List<GraduacaoResponse> historico(Long alunoId);

    FaixaAtualResponse faixaAtual(Long alunoId);

    List<FaixaResponse> faixas();

    /** Alunos elegiveis a nova graduacao por tempo na faixa (RF-072). */
    List<AlunoRef> elegiveis();

    /** Certificado em PDF de uma graduacao (RF-073). */
    byte[] gerarCertificado(Long graduacaoId);
}
