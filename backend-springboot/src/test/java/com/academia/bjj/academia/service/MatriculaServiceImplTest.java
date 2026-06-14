package com.academia.bjj.academia.service;

import com.academia.bjj.academia.dto.MatriculaRequest;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.model.Matricula;
import com.academia.bjj.academia.model.Plano;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.academia.model.Turma;
import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.academia.repository.ListaEsperaRepository;
import com.academia.bjj.academia.repository.MatriculaRepository;
import com.academia.bjj.academia.repository.PlanoRepository;
import com.academia.bjj.academia.repository.TurmaRepository;
import com.academia.bjj.academia.service.impl.MatriculaServiceImpl;
import com.academia.bjj.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regra critica de matricula (diretriz 11, criterio de aceitacao do SRS):
 * uma turma lotada nao aceita nova matricula (RF-047).
 */
@ExtendWith(MockitoExtension.class)
class MatriculaServiceImplTest {

    @Mock MatriculaRepository matriculaRepository;
    @Mock AlunoRepository alunoRepository;
    @Mock PlanoRepository planoRepository;
    @Mock TurmaRepository turmaRepository;
    @Mock ListaEsperaRepository listaEsperaRepository;
    @Mock AcademiaMapper mapper;

    MatriculaServiceImpl service;

    Aluno aluno;
    Plano plano;
    Turma turma;

    @BeforeEach
    void setup() {
        service = new MatriculaServiceImpl(matriculaRepository, alunoRepository, planoRepository,
                turmaRepository, listaEsperaRepository, mapper);

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");

        plano = new Plano();
        plano.setId(1L);

        turma = new Turma();
        turma.setId(10L);
        turma.setNome("Turma Lotada");
        turma.setCapacidade(20);
    }

    @Test
    void criar_deveFalhar_quandoTurmaLotada() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
        when(matriculaRepository.existsByAlunoIdAndStatus(1L, StatusMatricula.ATIVA)).thenReturn(false);
        when(turmaRepository.findById(10L)).thenReturn(Optional.of(turma));
        // Turma cheia: 20 ocupadas para capacidade 20.
        when(matriculaRepository.contarPorTurmaEStatus(10L, StatusMatricula.ATIVA)).thenReturn(20L);

        MatriculaRequest req = new MatriculaRequest(1L, 1L, LocalDate.now(), null, Set.of(10L));

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("lotada");

        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void criar_deveMatricular_quandoHaVaga() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
        when(matriculaRepository.existsByAlunoIdAndStatus(1L, StatusMatricula.ATIVA)).thenReturn(false);
        when(turmaRepository.findById(10L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.contarPorTurmaEStatus(10L, StatusMatricula.ATIVA)).thenReturn(5L);
        when(matriculaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(mapper.toResponse(any(Matricula.class))).thenReturn(null);

        MatriculaRequest req = new MatriculaRequest(1L, 1L, LocalDate.now(), "obs", Set.of(10L));
        service.criar(req);

        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    void criar_deveFalhar_quandoAlunoJaTemMatriculaAtiva() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
        when(matriculaRepository.existsByAlunoIdAndStatus(1L, StatusMatricula.ATIVA)).thenReturn(true);

        MatriculaRequest req = new MatriculaRequest(1L, 1L, LocalDate.now(), null, Set.of(10L));

        assertThatThrownBy(() -> service.criar(req))
                .hasMessageContaining("ja possui uma matricula ativa");
        verify(turmaRepository, never()).findById(eq(10L));
    }

    @Test
    void capacidadeZero_significaSemLimite() {
        turma.setCapacidade(0);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
        when(matriculaRepository.existsByAlunoIdAndStatus(1L, StatusMatricula.ATIVA)).thenReturn(false);
        when(turmaRepository.findById(10L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MatriculaRequest req = new MatriculaRequest(1L, 1L, LocalDate.now(), null, Set.of(10L));
        service.criar(req);

        // Nao deve nem consultar contagem quando capacidade e ilimitada.
        verify(matriculaRepository, never()).contarPorTurmaEStatus(any(), any());
        assertThat(turma.getCapacidade()).isZero();
    }
}
