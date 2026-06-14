package com.academia.bjj.academia.service.impl;

import com.academia.bjj.academia.dto.AlunoRequest;
import com.academia.bjj.academia.dto.AlunoResponse;
import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.repository.AlunoRepository;
import com.academia.bjj.academia.service.AlunoService;
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
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AcademiaMapper mapper;

    public AlunoServiceImpl(AlunoRepository repository,
                            UsuarioRepository usuarioRepository,
                            AcademiaMapper mapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public AlunoResponse criar(AlunoRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Ja existe um aluno com este e-mail");
        }
        Aluno a = new Aluno();
        aplicar(a, request);
        return mapper.toResponse(repository.save(a));
    }

    @Override
    @Transactional
    public AlunoResponse atualizar(Long id, AlunoRequest request) {
        Aluno a = obter(id);
        if (!a.getEmail().equalsIgnoreCase(request.email())
                && repository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Ja existe um aluno com este e-mail");
        }
        aplicar(a, request);
        return mapper.toResponse(repository.save(a));
    }

    @Override
    @Transactional(readOnly = true)
    public AlunoResponse buscar(Long id) {
        return mapper.toResponse(obter(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AlunoResponse meuPerfil(Long usuarioId) {
        return mapper.toResponse(repository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhum cadastro de aluno vinculado a este usuario")));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AlunoResponse> listar(String nome, Pageable pageable) {
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

    private Aluno obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno nao encontrado: " + id));
    }

    private void aplicar(Aluno a, AlunoRequest request) {
        a.setNome(request.nome().trim());
        a.setEmail(request.email().trim().toLowerCase());
        a.setTelefone(request.telefone());
        a.setDataNascimento(request.dataNascimento());
        a.setCpf(request.cpf());
        a.setContatoEmergencia(request.contatoEmergencia());
        a.setObservacoesSaude(request.observacoesSaude());
        a.setFaixaAtual(request.faixaAtual());
        if (request.ativo() != null) {
            a.setAtivo(request.ativo());
        }
        a.setUsuario(resolverUsuario(request.usuarioId()));
    }

    private Usuario resolverUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuario inexistente: " + usuarioId));
    }
}
