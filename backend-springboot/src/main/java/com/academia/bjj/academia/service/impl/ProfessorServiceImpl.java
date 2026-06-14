package com.academia.bjj.academia.service.impl;

import com.academia.bjj.academia.dto.ProfessorRequest;
import com.academia.bjj.academia.dto.ProfessorResponse;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Professor;
import com.academia.bjj.academia.repository.ProfessorRepository;
import com.academia.bjj.academia.service.ProfessorService;
import com.academia.bjj.auth.model.Usuario;
import com.academia.bjj.auth.repository.UsuarioRepository;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ConflictException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AcademiaMapper mapper;

    public ProfessorServiceImpl(ProfessorRepository repository,
                                UsuarioRepository usuarioRepository,
                                AcademiaMapper mapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProfessorResponse criar(ProfessorRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Ja existe um professor com este e-mail");
        }
        Professor p = new Professor();
        aplicar(p, request);
        return mapper.toResponse(repository.save(p));
    }

    @Override
    @Transactional
    public ProfessorResponse atualizar(Long id, ProfessorRequest request) {
        Professor p = obter(id);
        if (!p.getEmail().equalsIgnoreCase(request.email())
                && repository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Ja existe um professor com este e-mail");
        }
        aplicar(p, request);
        return mapper.toResponse(repository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorResponse buscar(Long id) {
        return mapper.toResponse(obter(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProfessorResponse> listar(String nome, Pageable pageable) {
        var page = (nome == null || nome.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @Override
    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private Professor obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor nao encontrado: " + id));
    }

    private void aplicar(Professor p, ProfessorRequest request) {
        p.setNome(request.nome().trim());
        p.setEmail(request.email().trim().toLowerCase());
        p.setTelefone(request.telefone());
        p.setFaixa(request.faixa());
        p.setBio(request.bio());
        if (request.ativo() != null) {
            p.setAtivo(request.ativo());
        }
        p.setUsuario(resolverUsuario(request.usuarioId()));
    }

    private Usuario resolverUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuario inexistente: " + usuarioId));
    }
}
