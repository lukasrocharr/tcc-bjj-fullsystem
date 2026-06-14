package com.academia.bjj.auditoria;

import com.academia.bjj.auditoria.model.Auditoria;
import com.academia.bjj.auditoria.repository.AuditoriaRepository;
import com.academia.bjj.auth.security.JwtAuthenticationFilter.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Registra automaticamente acoes sensiveis (mutacoes autenticadas) no log de
 * auditoria (RF-095). Executa apos o filtro JWT para ter o usuario no contexto.
 */
public class AuditoriaFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaFilter.class);
    private static final Set<String> METODOS_MUTANTES = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final AuditoriaRepository repository;

    public AuditoriaFilter(AuditoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
        try {
            registrar(request, response);
        } catch (Exception e) {
            // Auditoria nunca deve quebrar a requisicao principal.
            log.warn("Falha ao registrar auditoria: {}", e.getMessage());
        }
    }

    private void registrar(HttpServletRequest request, HttpServletResponse response) {
        if (!METODOS_MUTANTES.contains(request.getMethod())) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser user)) {
            return; // apenas acoes de usuarios autenticados
        }
        repository.save(new Auditoria(
                user.id(), user.email(), request.getMethod(),
                request.getRequestURI(), response.getStatus()));
    }
}
