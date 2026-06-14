package com.academia.bjj.academia.service.impl;

import com.academia.bjj.academia.dto.PlanoRequest;
import com.academia.bjj.academia.dto.PlanoResponse;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Modalidade;
import com.academia.bjj.academia.model.Plano;
import com.academia.bjj.academia.repository.ModalidadeRepository;
import com.academia.bjj.academia.repository.PlanoRepository;
import com.academia.bjj.academia.service.PlanoService;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class PlanoServiceImpl implements PlanoService {

    private final PlanoRepository repository;
    private final ModalidadeRepository modalidadeRepository;
    private final AcademiaMapper mapper;

    public PlanoServiceImpl(PlanoRepository repository,
                            ModalidadeRepository modalidadeRepository,
                            AcademiaMapper mapper) {
        this.repository = repository;
        this.modalidadeRepository = modalidadeRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PlanoResponse criar(PlanoRequest request) {
        Plano p = new Plano();
        aplicar(p, request);
        return mapper.toResponse(repository.save(p));
    }

    @Override
    @Transactional
    public PlanoResponse atualizar(Long id, PlanoRequest request) {
        Plano p = obter(id);
        aplicar(p, request);
        return mapper.toResponse(repository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public PlanoResponse buscar(Long id) {
        return mapper.toResponse(obter(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PlanoResponse> listar(Boolean ativo, Pageable pageable) {
        var page = (ativo == null)
                ? repository.findAll(pageable)
                : repository.findByAtivo(ativo, pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @Override
    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private Plano obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano nao encontrado: " + id));
    }

    private void aplicar(Plano p, PlanoRequest request) {
        p.setNome(request.nome().trim());
        p.setDescricao(request.descricao());
        p.setValor(request.valor());
        p.setPeriodicidade(request.periodicidade());
        p.setAulasPorSemana(request.aulasPorSemana());
        if (request.ativo() != null) {
            p.setAtivo(request.ativo());
        }
        p.setModalidades(resolverModalidades(request.modalidadeIds()));
    }

    private Set<Modalidade> resolverModalidades(Set<Long> ids) {
        Set<Modalidade> modalidades = new HashSet<>();
        if (ids == null) {
            return modalidades;
        }
        for (Long id : ids) {
            modalidades.add(modalidadeRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Modalidade inexistente: " + id)));
        }
        return modalidades;
    }
}
