package com.academia.bjj.graduacao.service.impl;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.model.Professor;
import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.academia.repository.ProfessorRepository;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.config.AppProperties;
import com.academia.bjj.graduacao.dto.FaixaAtualResponse;
import com.academia.bjj.graduacao.dto.FaixaResponse;
import com.academia.bjj.graduacao.dto.GraduacaoRequest;
import com.academia.bjj.graduacao.dto.GraduacaoResponse;
import com.academia.bjj.graduacao.mapper.GraduacaoMapper;
import com.academia.bjj.graduacao.model.Faixa;
import com.academia.bjj.graduacao.model.Graduacao;
import com.academia.bjj.graduacao.repository.FaixaRepository;
import com.academia.bjj.graduacao.repository.GraduacaoRepository;
import com.academia.bjj.graduacao.service.CertificadoPdfService;
import com.academia.bjj.graduacao.service.GraduacaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class GraduacaoServiceImpl implements GraduacaoService {

    private final GraduacaoRepository repository;
    private final FaixaRepository faixaRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final GraduacaoMapper mapper;
    private final AcademiaMapper academiaMapper;
    private final CertificadoPdfService certificadoPdfService;
    private final AppProperties props;
    private final Clock clock;

    public GraduacaoServiceImpl(GraduacaoRepository repository,
                                FaixaRepository faixaRepository,
                                AlunoRepository alunoRepository,
                                ProfessorRepository professorRepository,
                                GraduacaoMapper mapper,
                                AcademiaMapper academiaMapper,
                                CertificadoPdfService certificadoPdfService,
                                AppProperties props,
                                Clock clock) {
        this.repository = repository;
        this.faixaRepository = faixaRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.mapper = mapper;
        this.academiaMapper = academiaMapper;
        this.certificadoPdfService = certificadoPdfService;
        this.props = props;
        this.clock = clock;
    }

    @Override
    @Transactional
    public GraduacaoResponse registrar(GraduacaoRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno nao encontrado: " + request.alunoId()));
        Faixa faixa = faixaRepository.findById(request.faixaId())
                .orElseThrow(() -> new BusinessException("Faixa inexistente: " + request.faixaId()));

        if (request.graus() > faixa.getGrausMax()) {
            throw new BusinessException("Graus acima do maximo da faixa (" + faixa.getGrausMax() + ")");
        }

        Graduacao g = new Graduacao();
        g.setAluno(aluno);
        g.setFaixa(faixa);
        g.setGraus(request.graus());
        g.setData(request.data() != null ? request.data() : LocalDate.now(clock));
        g.setProfessor(resolverProfessor(request.professorId()));
        g.setObservacao(request.observacao());
        g = repository.save(g);

        // Atualiza a faixa atual desnormalizada do aluno (RF-071).
        aluno.setFaixaAtual(descricaoFaixa(faixa.getNome(), request.graus()));
        alunoRepository.save(aluno);

        return mapper.toResponse(g);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GraduacaoResponse> historico(Long alunoId) {
        return repository.findByAlunoIdOrderByDataDescIdDesc(alunoId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FaixaAtualResponse faixaAtual(Long alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Aluno nao encontrado: " + alunoId);
        }
        return repository.findFirstByAlunoIdOrderByDataDescIdDesc(alunoId)
                .map(g -> new FaixaAtualResponse(
                        alunoId,
                        g.getFaixa().getNome(),
                        g.getGraus(),
                        g.getData(),
                        ChronoUnit.DAYS.between(g.getData(), LocalDate.now(clock))))
                .orElse(new FaixaAtualResponse(alunoId, null, 0, null, 0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaixaResponse> faixas() {
        return faixaRepository.findAllByOrderByCategoriaAscOrdemAsc()
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlunoRef> elegiveis() {
        int minDias = props.getGraduacao().getDiasMinimosElegibilidade();
        LocalDate hoje = LocalDate.now(clock);
        List<AlunoRef> elegiveis = new ArrayList<>();
        for (Aluno aluno : alunoRepository.findByAtivoTrue()) {
            repository.findFirstByAlunoIdOrderByDataDescIdDesc(aluno.getId()).ifPresent(g -> {
                if (ChronoUnit.DAYS.between(g.getData(), hoje) >= minDias) {
                    elegiveis.add(academiaMapper.toRef(aluno));
                }
            });
        }
        return elegiveis;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] gerarCertificado(Long graduacaoId) {
        Graduacao g = repository.findById(graduacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Graduacao nao encontrada: " + graduacaoId));
        return certificadoPdfService.gerar(g);
    }

    private Professor resolverProfessor(Long professorId) {
        if (professorId == null) {
            return null;
        }
        return professorRepository.findById(professorId)
                .orElseThrow(() -> new BusinessException("Professor inexistente: " + professorId));
    }

    private String descricaoFaixa(String faixaNome, int graus) {
        return graus > 0 ? faixaNome + " (" + graus + "o grau)" : faixaNome;
    }
}
