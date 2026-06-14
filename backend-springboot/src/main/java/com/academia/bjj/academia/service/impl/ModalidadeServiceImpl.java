package com.academia.bjj.academia.service.impl;

import com.academia.bjj.academia.dto.ModalidadeRequest;
import com.academia.bjj.academia.dto.ModalidadeResponse;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Modalidade;
import com.academia.bjj.academia.repository.ModalidadeRepository;
import com.academia.bjj.academia.service.ModalidadeService;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.ConflictException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModalidadeServiceImpl implements ModalidadeService {

    private final ModalidadeRepository repository;
    private final AcademiaMapper mapper;

    public ModalidadeServiceImpl(ModalidadeRepository repository, AcademiaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ModalidadeResponse criar(ModalidadeRequest request) {
        if (repository.existsByNomeIgnoreCase(request.nome())) {
            throw new ConflictException("Ja existe uma modalidade com este nome");
        }
        Modalidade m = new Modalidade();
        aplicar(m, request);
        return mapper.toResponse(repository.save(m));
    }

    @Override
    @Transactional
    public ModalidadeResponse atualizar(Long id, ModalidadeRequest request) {
        Modalidade m = obter(id);
        aplicar(m, request);
        return mapper.toResponse(repository.save(m));
    }

    @Override
    @Transactional(readOnly = true)
    public ModalidadeResponse buscar(Long id) {
        return mapper.toResponse(obter(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ModalidadeResponse> listar(Boolean ativo, Pageable pageable) {
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

    private Modalidade obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modalidade nao encontrada: " + id));
    }

    private void aplicar(Modalidade m, ModalidadeRequest request) {
        m.setNome(request.nome().trim());
        m.setDescricao(request.descricao());
        if (request.ativo() != null) {
            m.setAtivo(request.ativo());
        }
    }
}
