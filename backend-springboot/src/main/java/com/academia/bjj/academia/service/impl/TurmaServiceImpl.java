package com.academia.bjj.academia.service.impl;

import com.academia.bjj.academia.dto.TurmaRequest;
import com.academia.bjj.academia.dto.TurmaResponse;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Modalidade;
import com.academia.bjj.academia.model.Professor;
import com.academia.bjj.academia.model.StatusMatricula;
import com.academia.bjj.academia.model.Turma;
import com.academia.bjj.academia.repository.MatriculaRepository;
import com.academia.bjj.academia.repository.ModalidadeRepository;
import com.academia.bjj.academia.repository.ProfessorRepository;
import com.academia.bjj.academia.repository.TurmaRepository;
import com.academia.bjj.academia.service.TurmaService;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurmaServiceImpl implements TurmaService {

    private final TurmaRepository repository;
    private final ModalidadeRepository modalidadeRepository;
    private final ProfessorRepository professorRepository;
    private final MatriculaRepository matriculaRepository;
    private final AcademiaMapper mapper;

    public TurmaServiceImpl(TurmaRepository repository,
                            ModalidadeRepository modalidadeRepository,
                            ProfessorRepository professorRepository,
                            MatriculaRepository matriculaRepository,
                            AcademiaMapper mapper) {
        this.repository = repository;
        this.modalidadeRepository = modalidadeRepository;
        this.professorRepository = professorRepository;
        this.matriculaRepository = matriculaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public TurmaResponse criar(TurmaRequest request) {
        Turma t = new Turma();
        aplicar(t, request);
        return toResponse(repository.save(t));
    }

    @Override
    @Transactional
    public TurmaResponse atualizar(Long id, TurmaRequest request) {
        Turma t = obter(id);
        aplicar(t, request);
        return toResponse(repository.save(t));
    }

    @Override
    @Transactional(readOnly = true)
    public TurmaResponse buscar(Long id) {
        return toResponse(obter(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TurmaResponse> listar(Long modalidadeId, Boolean ativo, Pageable pageable) {
        var page = repository.buscar(modalidadeId, ativo, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurmaResponse> grade() {
        return repository.findAllByOrderByDiaSemanaAscHoraInicioAsc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private Turma obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma nao encontrada: " + id));
    }

    private TurmaResponse toResponse(Turma t) {
        long ocupadas = matriculaRepository.contarPorTurmaEStatus(t.getId(), StatusMatricula.ATIVA);
        return mapper.toResponse(t, ocupadas);
    }

    private void aplicar(Turma t, TurmaRequest request) {
        if (request.horaFim().isBefore(request.horaInicio()) || request.horaFim().equals(request.horaInicio())) {
            throw new BusinessException("A hora de fim deve ser posterior a hora de inicio");
        }
        Modalidade modalidade = modalidadeRepository.findById(request.modalidadeId())
                .orElseThrow(() -> new BusinessException("Modalidade inexistente: " + request.modalidadeId()));
        t.setNome(request.nome().trim());
        t.setModalidade(modalidade);
        t.setProfessor(resolverProfessor(request.professorId()));
        t.setDiaSemana(request.diaSemana());
        t.setHoraInicio(request.horaInicio());
        t.setHoraFim(request.horaFim());
        t.setCapacidade(request.capacidade());
        t.setNivel(request.nivel());
        if (request.ativo() != null) {
            t.setAtivo(request.ativo());
        }
    }

    private Professor resolverProfessor(Long professorId) {
        if (professorId == null) {
            return null;
        }
        return professorRepository.findById(professorId)
                .orElseThrow(() -> new BusinessException("Professor inexistente: " + professorId));
    }
}
