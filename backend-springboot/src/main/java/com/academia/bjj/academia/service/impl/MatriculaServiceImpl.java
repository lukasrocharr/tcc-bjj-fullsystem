package com.academia.bjj.academia.service.impl;

import com.academia.bjj.academia.dto.ListaEsperaResponse;
import com.academia.bjj.academia.dto.MatriculaRequest;
import com.academia.bjj.academia.dto.MatriculaResponse;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.model.ListaEspera;
import com.academia.bjj.academia.model.Matricula;
import com.academia.bjj.academia.model.Plano;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.academia.model.Turma;
import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.academia.repository.ListaEsperaRepository;
import com.academia.bjj.academia.repository.MatriculaRepository;
import com.academia.bjj.academia.repository.PlanoRepository;
import com.academia.bjj.academia.repository.TurmaRepository;
import com.academia.bjj.academia.service.MatriculaService;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ConflictException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Regras de matricula (RF-046 a RF-048): vincula aluno+plano+turmas, valida
 * capacidade das turmas, gere status (suspensao/cancelamento) e lista de espera.
 */
@Service
public class MatriculaServiceImpl implements MatriculaService {

    private final MatriculaRepository repository;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final TurmaRepository turmaRepository;
    private final ListaEsperaRepository listaEsperaRepository;
    private final AcademiaMapper mapper;

    public MatriculaServiceImpl(MatriculaRepository repository,
                                AlunoRepository alunoRepository,
                                PlanoRepository planoRepository,
                                TurmaRepository turmaRepository,
                                ListaEsperaRepository listaEsperaRepository,
                                AcademiaMapper mapper) {
        this.repository = repository;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.turmaRepository = turmaRepository;
        this.listaEsperaRepository = listaEsperaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public MatriculaResponse criar(MatriculaRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new BusinessException("Aluno inexistente: " + request.alunoId()));
        Plano plano = planoRepository.findById(request.planoId())
                .orElseThrow(() -> new BusinessException("Plano inexistente: " + request.planoId()));

        if (repository.existsByAlunoIdAndStatus(aluno.getId(), StatusMatricula.ATIVA)) {
            throw new ConflictException("O aluno ja possui uma matricula ativa");
        }

        Set<Turma> turmas = resolverTurmas(request.turmaIds());
        for (Turma turma : turmas) {
            validarCapacidade(turma);
        }

        Matricula m = new Matricula();
        m.setAluno(aluno);
        m.setPlano(plano);
        m.setStatus(StatusMatricula.ATIVA);
        m.setDataInicio(request.dataInicio() != null ? request.dataInicio() : LocalDate.now());
        m.setObservacao(request.observacao());
        m.setTurmas(turmas);

        return mapper.toResponse(repository.save(m));
    }

    @Override
    @Transactional(readOnly = true)
    public MatriculaResponse buscar(Long id) {
        return mapper.toResponse(obter(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MatriculaResponse> listar(Long alunoId, StatusMatricula status, Pageable pageable) {
        return PageResponse.from(repository.buscar(alunoId, status, pageable).map(mapper::toResponse));
    }

    @Override
    @Transactional
    public MatriculaResponse alterarStatus(Long id, StatusMatricula novoStatus) {
        Matricula m = obter(id);
        if (m.getStatus() == novoStatus) {
            return mapper.toResponse(m);
        }
        // Reativar exige que as turmas ainda tenham vaga.
        if (novoStatus == StatusMatricula.ATIVA) {
            for (Turma turma : m.getTurmas()) {
                validarCapacidade(turma);
            }
            m.setDataFim(null);
        } else if (novoStatus == StatusMatricula.CANCELADA) {
            m.setDataFim(LocalDate.now());
        }
        m.setStatus(novoStatus);
        return mapper.toResponse(repository.save(m));
    }

    @Override
    @Transactional
    public ListaEsperaResponse entrarListaEspera(Long turmaId, Long alunoId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma nao encontrada: " + turmaId));
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new BusinessException("Aluno inexistente: " + alunoId));

        if (listaEsperaRepository.existsByTurmaIdAndAlunoId(turmaId, alunoId)) {
            throw new ConflictException("O aluno ja esta na lista de espera desta turma");
        }

        ListaEspera entrada = new ListaEspera();
        entrada.setTurma(turma);
        entrada.setAluno(aluno);
        entrada.setPosicao((int) listaEsperaRepository.countByTurmaId(turmaId) + 1);
        return mapper.toResponse(listaEsperaRepository.save(entrada));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListaEsperaResponse> listaEspera(Long turmaId) {
        return listaEsperaRepository.findByTurmaIdOrderByPosicaoAsc(turmaId)
                .stream().map(mapper::toResponse).toList();
    }

    // ---------------- helpers ----------------

    private Matricula obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula nao encontrada: " + id));
    }

    private Set<Turma> resolverTurmas(Set<Long> ids) {
        Set<Turma> turmas = new HashSet<>();
        for (Long id : ids) {
            turmas.add(turmaRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Turma inexistente: " + id)));
        }
        return turmas;
    }

    private void validarCapacidade(Turma turma) {
        if (turma.getCapacidade() <= 0) {
            return; // capacidade 0 = sem limite
        }
        long ocupadas = repository.contarPorTurmaEStatus(turma.getId(), StatusMatricula.ATIVA);
        if (ocupadas >= turma.getCapacidade()) {
            throw new BusinessException("Turma '" + turma.getNome()
                    + "' esta lotada. Use a lista de espera.");
        }
    }
}
