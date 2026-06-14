package com.academia.bjj.ecommerce.service.impl;

import com.academia.bjj.common.exception.ConflictException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.ecommerce.dto.CategoriaRequest;
import com.academia.bjj.ecommerce.dto.CategoriaResponse;
import com.academia.bjj.ecommerce.mapper.EcommerceMapper;
import com.academia.bjj.ecommerce.model.Categoria;
import com.academia.bjj.ecommerce.repository.CategoriaRepository;
import com.academia.bjj.ecommerce.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final EcommerceMapper mapper;

    public CategoriaServiceImpl(CategoriaRepository repository, EcommerceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        if (repository.existsByNomeIgnoreCase(request.nome())) {
            throw new ConflictException("Ja existe uma categoria com este nome");
        }
        Categoria c = new Categoria();
        c.setNome(request.nome().trim());
        c.setAtivo(request.ativo() == null || request.ativo());
        return mapper.toResponse(repository.save(c));
    }

    @Override
    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada: " + id));
        c.setNome(request.nome().trim());
        if (request.ativo() != null) {
            c.setAtivo(request.ativo());
        }
        return mapper.toResponse(repository.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Categoria c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada: " + id));
        repository.delete(c);
    }
}
