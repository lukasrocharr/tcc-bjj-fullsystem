package com.academia.bjj.notificacao;

import com.academia.bjj.auth.model.Usuario;
import com.academia.bjj.auth.repository.UsuarioRepository;
import com.academia.bjj.common.dto.PageResponse;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.notificacao.dto.ComunicadoRequest;
import com.academia.bjj.notificacao.dto.NotificacaoResponse;
import com.academia.bjj.notificacao.model.Notificacao;
import com.academia.bjj.notificacao.repository.NotificacaoRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Notificacoes in-app (RF-103) e comunicados em massa (RF-104).
 */
@Service
public class InAppNotificacaoService {

    private final NotificacaoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final NotificationService emailService;

    public InAppNotificacaoService(NotificacaoRepository repository,
                                   UsuarioRepository usuarioRepository,
                                   NotificationService emailService) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void criar(Usuario usuario, String titulo, String mensagem) {
        repository.save(new Notificacao(usuario, titulo, mensagem));
    }

    @Transactional
    public int enviarComunicado(ComunicadoRequest request) {
        List<Usuario> destinatarios = request.papel() == null
                ? usuarioRepository.findAll()
                : usuarioRepository.findByPapeis_Nome(request.papel());

        for (Usuario u : destinatarios) {
            repository.save(new Notificacao(u, request.titulo(), request.mensagem()));
            if (request.enviarEmail()) {
                emailService.enviarEmail(u.getEmail(), request.titulo(), request.mensagem());
            }
        }
        return destinatarios.size();
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificacaoResponse> listar(Long usuarioId, Pageable pageable) {
        return PageResponse.from(repository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId, pageable)
                .map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long usuarioId) {
        return repository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public void marcarLida(Long usuarioId, Long id) {
        Notificacao n = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacao nao encontrada: " + id));
        if (n.getUsuario().getId().equals(usuarioId)) {
            n.setLida(true);
            repository.save(n);
        }
    }

    @Transactional
    public int marcarTodasLidas(Long usuarioId) {
        return repository.marcarTodasLidas(usuarioId);
    }

    private NotificacaoResponse toResponse(Notificacao n) {
        return new NotificacaoResponse(n.getId(), n.getTitulo(), n.getMensagem(), n.isLida(), n.getCreatedAt());
    }
}
