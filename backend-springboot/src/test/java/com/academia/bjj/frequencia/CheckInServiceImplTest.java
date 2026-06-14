package com.academia.bjj.frequencia;

import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.model.DiaSemana;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.academia.model.Turma;
import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.academia.repository.MatriculaRepository;
import com.academia.bjj.academia.repository.TurmaRepository;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.config.AppProperties;
import com.academia.bjj.frequencia.dto.CheckInRequest;
import com.academia.bjj.frequencia.mapper.FrequenciaMapper;
import com.academia.bjj.frequencia.model.CheckIn;
import com.academia.bjj.frequencia.repository.CheckInRepository;
import com.academia.bjj.frequencia.service.impl.CheckInServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regra critica de check-in (criterio de aceitacao do SRS): so registra dentro
 * da janela de horario, no dia correto, com matricula ativa e sem duplicidade
 * (RF-057). Usa um {@link Clock} fixo para tornar o tempo deterministico.
 */
@ExtendWith(MockitoExtension.class)
class CheckInServiceImplTest {

    @Mock CheckInRepository checkInRepository;
    @Mock AlunoRepository alunoRepository;
    @Mock TurmaRepository turmaRepository;
    @Mock MatriculaRepository matriculaRepository;
    @Mock FrequenciaMapper mapper;
    @Mock AcademiaMapper academiaMapper;

    AppProperties props = new AppProperties();
    CheckInServiceImpl service;

    final ZoneId zone = ZoneId.systemDefault();
    // Quarta-feira fixa as 18:45.
    final LocalDateTime momento = LocalDateTime.of(2026, 6, 17, 18, 45);
    Clock clock;
    DiaSemana hojeDia;

    Aluno aluno;
    Turma turma;

    @BeforeEach
    void setup() {
        clock = Clock.fixed(momento.atZone(zone).toInstant(), zone);
        service = new CheckInServiceImpl(checkInRepository, alunoRepository, turmaRepository,
                matriculaRepository, mapper, academiaMapper, props, clock);
        hojeDia = paraDiaSemana(momento.toLocalDate().getDayOfWeek());

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno");

        turma = new Turma();
        turma.setId(10L);
        turma.setNome("Turma");
        turma.setDiaSemana(hojeDia);
        turma.setHoraInicio(LocalTime.of(19, 0));
        turma.setHoraFim(LocalTime.of(20, 0));
    }

    private CheckInRequest req() {
        return new CheckInRequest(1L, 10L);
    }

    @Test
    void checkIn_valido_dentroDaJanela_registra() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(10L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.existeMatriculaComAlunoTurmaEStatus(1L, 10L, StatusMatricula.ATIVA))
                .thenReturn(true);
        when(checkInRepository.existsByAlunoIdAndTurmaIdAndData(any(), any(), any())).thenReturn(false);
        when(checkInRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        lenient().when(mapper.toResponse(any(CheckIn.class))).thenReturn(null);

        service.registrar(req());

        verify(checkInRepository).save(any(CheckIn.class));
    }

    @Test
    void checkIn_foraDaJanela_lancaErro() {
        // Clock as 21:00 (apos o fim da turma).
        Clock tarde = Clock.fixed(momento.toLocalDate().atTime(21, 0).atZone(zone).toInstant(), zone);
        service = new CheckInServiceImpl(checkInRepository, alunoRepository, turmaRepository,
                matriculaRepository, mapper, academiaMapper, props, tarde);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(10L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.existeMatriculaComAlunoTurmaEStatus(1L, 10L, StatusMatricula.ATIVA))
                .thenReturn(true);

        assertThatThrownBy(() -> service.registrar(req()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("janela");
        verify(checkInRepository, never()).save(any());
    }

    @Test
    void checkIn_diaErrado_lancaErro() {
        turma.setDiaSemana(outroDia(hojeDia));
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(10L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.existeMatriculaComAlunoTurmaEStatus(1L, 10L, StatusMatricula.ATIVA))
                .thenReturn(true);

        assertThatThrownBy(() -> service.registrar(req()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("nao ocorre hoje");
        verify(checkInRepository, never()).save(any());
    }

    @Test
    void checkIn_semMatriculaAtiva_lancaErro() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(10L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.existeMatriculaComAlunoTurmaEStatus(1L, 10L, StatusMatricula.ATIVA))
                .thenReturn(false);

        assertThatThrownBy(() -> service.registrar(req()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("matricula ativa");
        verify(checkInRepository, never()).save(any());
    }

    // mapeamento espelhando o do servico
    private DiaSemana paraDiaSemana(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> DiaSemana.SEGUNDA;
            case TUESDAY -> DiaSemana.TERCA;
            case WEDNESDAY -> DiaSemana.QUARTA;
            case THURSDAY -> DiaSemana.QUINTA;
            case FRIDAY -> DiaSemana.SEXTA;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

    private DiaSemana outroDia(DiaSemana atual) {
        return atual == DiaSemana.SEGUNDA ? DiaSemana.TERCA : DiaSemana.SEGUNDA;
    }
}
