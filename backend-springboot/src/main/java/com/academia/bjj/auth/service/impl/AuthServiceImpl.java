package com.academia.bjj.auth.service.impl;

import com.academia.bjj.auth.dto.AuthResponse;
import com.academia.bjj.auth.dto.ForgotPasswordRequest;
import com.academia.bjj.auth.dto.LoginRequest;
import com.academia.bjj.auth.dto.RefreshRequest;
import com.academia.bjj.auth.dto.RegisterRequest;
import com.academia.bjj.auth.dto.ResetPasswordRequest;
import com.academia.bjj.auth.dto.UsuarioResponse;
import com.academia.bjj.auth.mapper.UsuarioMapper;
import com.academia.bjj.auth.service.AuthService;
import com.academia.bjj.auth.model.Papel;
import com.academia.bjj.auth.model.PapelNome;
import com.academia.bjj.auth.model.PasswordResetToken;
import com.academia.bjj.auth.model.RefreshToken;
import com.academia.bjj.auth.model.Usuario;
import com.academia.bjj.auth.repository.PapelRepository;
import com.academia.bjj.auth.repository.PasswordResetTokenRepository;
import com.academia.bjj.auth.repository.RefreshTokenRepository;
import com.academia.bjj.auth.repository.UsuarioRepository;
import com.academia.bjj.auth.security.JwtService;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ConflictException;
import com.academia.bjj.common.exception.ResourceNotFoundException;
import com.academia.bjj.config.AppProperties;
import com.academia.bjj.notificacao.NotificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementacao dos casos de uso de autenticacao (RF-096 a RF-101):
 * registro com hash BCrypt, login com bloqueio temporario por tentativas,
 * emissao de JWT + refresh token rotacionavel e recuperacao de senha.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;
    private final NotificationService notificationService;
    private final AppProperties props;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PapelRepository papelRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           UsuarioMapper usuarioMapper,
                           NotificationService notificationService,
                           AppProperties props) {
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioMapper = usuarioMapper;
        this.notificationService = notificationService;
        this.props = props;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Ja existe um usuario com este e-mail");
        }

        // Auto-registro publico recebe o papel ALUNO (papeis administrativos
        // sao atribuidos por um ADMIN em endpoints de gestao, fases futuras).
        Papel papelAluno = papelRepository.findByNome(PapelNome.ALUNO)
                .orElseThrow(() -> new BusinessException("Papel ALUNO nao configurado"));

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.addPapel(papelAluno);
        usuario = usuarioRepository.save(usuario);

        notificationService.enviarEmail(usuario.getEmail(), "Bem-vindo a academia",
                "Ola " + usuario.getNome() + ", seu cadastro foi realizado com sucesso!");

        return emitirTokens(usuario);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BusinessException("Credenciais invalidas"));

        if (estaBloqueado(usuario)) {
            throw new BusinessException("Conta temporariamente bloqueada. Tente novamente mais tarde.");
        }
        if (!usuario.isAtivo()) {
            throw new BusinessException("Conta inativa. Contate a administracao.");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            registrarFalhaLogin(usuario);
            throw new BusinessException("Credenciais invalidas");
        }

        // Sucesso: zera tentativas e desbloqueia.
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAte(null);
        usuarioRepository.save(usuario);

        return emitirTokens(usuario);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken atual = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BusinessException("Refresh token invalido"));

        if (!atual.isValido()) {
            throw new BusinessException("Refresh token expirado ou revogado");
        }

        // Rotacao: revoga o atual e emite um novo par.
        atual.setRevogado(true);
        refreshTokenRepository.save(atual);

        return emitirTokens(atual.getUsuario());
    }

    @Override
    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByToken(request.refreshToken()).ifPresent(rt -> {
            rt.setRevogado(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Resposta sempre 204 para nao revelar se o e-mail existe (anti-enumeracao).
        usuarioRepository.findByEmailIgnoreCase(request.email()).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            OffsetDateTime expira = OffsetDateTime.now()
                    .plusMinutes(props.getSecurity().getPasswordResetTokenExpirationMinutes());
            passwordResetTokenRepository.save(new PasswordResetToken(token, usuario, expira));

            notificationService.enviarEmail(usuario.getEmail(), "Recuperacao de senha",
                    "Use o token a seguir para redefinir sua senha: " + token
                            + "\nValido por " + props.getSecurity().getPasswordResetTokenExpirationMinutes()
                            + " minutos.");
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new BusinessException("Token de redefinicao invalido"));

        if (!token.isValido()) {
            throw new BusinessException("Token de redefinicao expirado ou ja utilizado");
        }

        Usuario usuario = token.getUsuario();
        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAte(null);
        usuarioRepository.save(usuario);

        token.setUsado(true);
        passwordResetTokenRepository.save(token);

        // Invalida sessoes ativas apos troca de senha.
        refreshTokenRepository.revogarTodosDoUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse currentUser(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
        return usuarioMapper.toResponse(usuario);
    }

    // ---------------- helpers ----------------

    private boolean estaBloqueado(Usuario usuario) {
        return usuario.getBloqueadoAte() != null
                && usuario.getBloqueadoAte().isAfter(OffsetDateTime.now());
    }

    private void registrarFalhaLogin(Usuario usuario) {
        int tentativas = usuario.getTentativasLogin() + 1;
        usuario.setTentativasLogin(tentativas);
        if (tentativas >= props.getSecurity().getMaxLoginAttempts()) {
            usuario.setBloqueadoAte(OffsetDateTime.now()
                    .plusMinutes(props.getSecurity().getLockoutMinutes()));
            usuario.setTentativasLogin(0);
        }
        usuarioRepository.save(usuario);
    }

    private AuthResponse emitirTokens(Usuario usuario) {
        List<String> authorities = usuario.getPapeis().stream()
                .map(p -> p.getNome().authority())
                .toList();

        String accessToken = jwtService.generateAccessToken(
                usuario.getId(), usuario.getEmail(), authorities);

        String refreshValue = UUID.randomUUID().toString();
        OffsetDateTime refreshExp = OffsetDateTime.now()
                .plusDays(props.getJwt().getRefreshTokenExpirationDays());
        refreshTokenRepository.save(new RefreshToken(refreshValue, usuario, refreshExp));

        long expiresInSeconds = props.getJwt().getAccessTokenExpirationMinutes() * 60;
        return AuthResponse.of(accessToken, refreshValue, expiresInSeconds,
                usuarioMapper.toResponse(usuario));
    }
}
