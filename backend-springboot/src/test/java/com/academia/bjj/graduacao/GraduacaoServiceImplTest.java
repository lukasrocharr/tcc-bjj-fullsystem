package com.academia.bjj.graduacao;

import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.academia.repository.ProfessorRepository;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.config.AppProperties;
import com.academia.bjj.graduacao.dto.GraduacaoRequest;
import com.academia.bjj.graduacao.model.CategoriaFaixa;
import com.academia.bjj.graduacao.model.Faixa;
import com.academia.bjj.graduacao.model.Graduacao;
import com.academia.bjj.graduacao.repository.FaixaRepository;
import com.academia.bjj.graduacao.repository.GraduacaoRepository;
import com.academia.bjj.graduacao.service.CertificadoPdfService;
import com.academia.bjj.graduacao.service.impl.GraduacaoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Criterio de aceitacao do SRS: registrar graduacao atualiza imediatamente a
 * faixa atual do aluno (RF-071) e grava no historico append-only (RF-070).
 */
@ExtendWith(MockitoExtension.class)
class GraduacaoServiceImplTest {

    @Mock GraduacaoRepository repository;
    @Mock FaixaRepository faixaRepository;
    @Mock AlunoRepository alunoRepository;
    @Mock ProfessorRepository professorRepository;
    @Mock com.academia.bjj.graduacao.mapper.GraduacaoMapper mapper;
    @Mock AcademiaMapper academiaMapper;
    @Mock CertificadoPdfService certificadoPdfService;

    AppProperties props = new AppProperties();
    Clock clock = Clock.fixed(LocalDate.of(2026, 6, 13).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());
    GraduacaoServiceImpl service;

    Aluno aluno;
    Faixa azul;

    @BeforeEach
    void setup() {
        service = new GraduacaoServiceImpl(repository, faixaRepository, alunoRepository,
                professorRepository, mapper, academiaMapper, certificadoPdfService, props, clock);

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno");
        aluno.setFaixaAtual("Branca");

        azul = new Faixa();
        azul.setId(2L);
        azul.setNome("Azul");
        azul.setCategoria(CategoriaFaixa.ADULTO);
        azul.setOrdem(2);
        azul.setGrausMax(4);
    }

    @Test
    void registrar_atualizaFaixaAtualDoAluno_eGravaHistorico() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(faixaRepository.findById(2L)).thenReturn(Optional.of(azul));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(alunoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        lenient().when(mapper.toResponse(any(Graduacao.class))).thenReturn(null);

        service.registrar(new GraduacaoRequest(1L, 2L, 0, LocalDate.of(2026, 6, 13), null, "Parabens"));

        verify(repository).save(any(Graduacao.class));
        verify(alunoRepository).save(aluno);
        assertThat(aluno.getFaixaAtual()).isEqualTo("Azul");
    }

    @Test
    void registrar_comGrausAcimaDoMaximo_lancaErro() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(faixaRepository.findById(2L)).thenReturn(Optional.of(azul));

        assertThatThrownBy(() -> service.registrar(
                new GraduacaoRequest(1L, 2L, 9, null, null, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("maximo");
        verify(repository, never()).save(any());
    }
}
