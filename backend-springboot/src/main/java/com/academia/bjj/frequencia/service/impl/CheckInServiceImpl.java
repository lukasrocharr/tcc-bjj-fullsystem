package com.academia.bjj.frequencia.service.impl;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.model.DiaSemana;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.academia.model.Turma;
import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.academia.repository.MatriculaRepository;
import com.academia.bjj.academia.repository.TurmaRepository;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ConflictException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.config.AppProperties;
import com.academia.bjj.frequencia.dto.ChamadaRequest;
import com.academia.bjj.frequencia.dto.CheckInRequest;
import com.academia.bjj.frequencia.dto.CheckInResponse;
import com.academia.bjj.frequencia.dto.FrequenciaResponse;
import com.academia.bjj.frequencia.mapper.FrequenciaMapper;
import com.academia.bjj.frequencia.model.CheckIn;
import com.academia.bjj.frequencia.model.OrigemCheckIn;
import com.academia.bjj.frequencia.repository.CheckInRepository;
import com.academia.bjj.frequencia.service.CheckInService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRepository checkInRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;
    private final FrequenciaMapper mapper;
    private final AcademiaMapper academiaMapper;
    private final AppProperties props;
    private final Clock clock;

    public CheckInServiceImpl(CheckInRepository checkInRepository,
                              AlunoRepository alunoRepository,
                              TurmaRepository turmaRepository,
                              MatriculaRepository matriculaRepository,
                              FrequenciaMapper mapper,
                              AcademiaMapper academiaMapper,
                              AppProperties props,
                              Clock clock) {
        this.checkInRepository = checkInRepository;
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.matriculaRepository = matriculaRepository;
        this.mapper = mapper;
        this.academiaMapper = academiaMapper;
        this.props = props;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CheckInResponse registrar(CheckInRequest request) {
        Aluno aluno = obterAluno(request.alunoId());
        Turma turma = obterTurma(request.turmaId());

        if (!matriculaAtivaContemTurma(aluno.getId(), turma.getId())) {
            throw new BusinessException("Aluno nao possui matricula ativa nesta turma");
        }

        OffsetDateTime agora = OffsetDateTime.now(clock);
        LocalDate hoje = agora.toLocalDate();

        validarDiaDaSemana(turma, hoje);
        validarJanelaHorario(turma, agora.toLocalTime());

        if (checkInRepository.existsByAlunoIdAndTurmaIdAndData(aluno.getId(), turma.getId(), hoje)) {
            throw new ConflictException("Check-in ja registrado para esta turma hoje");
        }

        CheckIn checkIn = new CheckIn(aluno, turma, hoje, agora, OrigemCheckIn.SELF);
        return mapper.toResponse(checkInRepository.save(checkIn));
    }

    @Override
    @Transactional
    public List<CheckInResponse> registrarChamada(ChamadaRequest request) {
        Turma turma = obterTurma(request.turmaId());
        LocalDate data = request.data() != null ? request.data() : LocalDate.now(clock);
        OffsetDateTime agora = OffsetDateTime.now(clock);

        List<CheckInResponse> registrados = new ArrayList<>();
        for (Long alunoId : request.alunoIds()) {
            Aluno aluno = obterAluno(alunoId);
            if (!matriculaAtivaContemTurma(alunoId, turma.getId())) {
                throw new BusinessException("Aluno " + aluno.getNome()
                        + " nao possui matricula ativa nesta turma");
            }
            if (checkInRepository.existsByAlunoIdAndTurmaIdAndData(alunoId, turma.getId(), data)) {
                continue; // ja presente: ignora sem erro
            }
            CheckIn checkIn = new CheckIn(aluno, turma, data, agora, OrigemCheckIn.PROFESSOR);
            registrados.add(mapper.toResponse(checkInRepository.save(checkIn)));
        }
        return registrados;
    }

    @Override
    @Transactional(readOnly = true)
    public FrequenciaResponse frequenciaDoAluno(Long alunoId) {
        obterAluno(alunoId);
        List<CheckIn> checkins = checkInRepository.findByAlunoIdOrderByDataDesc(alunoId);

        long total = checkins.size();
        LocalDate hoje = LocalDate.now(clock);
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        long noMes = checkInRepository.countByAlunoIdAndDataBetween(alunoId, inicioMes, hoje);

        TreeSet<LocalDate> diasDistintos = new TreeSet<>();
        checkins.forEach(c -> diasDistintos.add(c.getData()));

        LocalDate ultimo = checkins.isEmpty() ? null : checkins.get(0).getData();
        long streak = calcularStreak(diasDistintos, hoje);

        return new FrequenciaResponse(alunoId, total, noMes, diasDistintos.size(), ultimo, streak);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInResponse> historicoDoAluno(Long alunoId) {
        obterAluno(alunoId);
        return checkInRepository.findByAlunoIdOrderByDataDesc(alunoId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlunoRef> alertasBaixaFrequencia() {
        LocalDate corte = LocalDate.now(clock).minusDays(props.getFrequencia().getBaixaFrequenciaDias());
        List<Long> ids = checkInRepository.idsAlunosAtivosSemCheckInDesde(corte);
        return alunoRepository.findAllById(ids).stream()
                .map(academiaMapper::toRef)
                .toList();
    }

    // ---------------- helpers ----------------

    private Aluno obterAluno(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno nao encontrado: " + id));
    }

    private Turma obterTurma(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma nao encontrada: " + id));
    }

    private boolean matriculaAtivaContemTurma(Long alunoId, Long turmaId) {
        return matriculaRepository.existeMatriculaComAlunoTurmaEStatus(
                alunoId, turmaId, StatusMatricula.ATIVA);
    }

    private void validarDiaDaSemana(Turma turma, LocalDate dia) {
        if (paraDiaSemana(dia.getDayOfWeek()) != turma.getDiaSemana()) {
            throw new BusinessException("A turma nao ocorre hoje (" + turma.getDiaSemana() + ")");
        }
    }

    private void validarJanelaHorario(Turma turma, LocalTime agora) {
        LocalTime abertura = turma.getHoraInicio().minusMinutes(props.getFrequencia().getJanelaAntesMinutos());
        if (agora.isBefore(abertura) || agora.isAfter(turma.getHoraFim())) {
            throw new BusinessException("Fora da janela de check-in (" + abertura + " - " + turma.getHoraFim() + ")");
        }
    }

    private long calcularStreak(TreeSet<LocalDate> dias, LocalDate hoje) {
        // Sequencia de dias consecutivos terminando em hoje ou ontem.
        LocalDate ref = hoje;
        if (!dias.contains(ref)) {
            ref = hoje.minusDays(1);
            if (!dias.contains(ref)) {
                return 0;
            }
        }
        long streak = 0;
        while (dias.contains(ref)) {
            streak++;
            ref = ref.minusDays(1);
        }
        return streak;
    }

    private DiaSemana paraDiaSemana(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> DiaSemana.SEGUNDA;
            case TUESDAY -> DiaSemana.TERCA;
            case WEDNESDAY -> DiaSemana.QUARTA;
            case THURSDAY -> DiaSemana.QUINTA;
            case FRIDAY -> DiaSemana.SEXTA;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }
}
