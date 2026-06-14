package com.academia.bjj.auth.service;

import com.academia.bjj.auth.dto.LoginRequest;
import com.academia.bjj.auth.dto.RegisterRequest;
import com.academia.bjj.auth.dto.UsuarioResponse;
import com.academia.bjj.auth.mapper.UsuarioMapper;
import com.academia.bjj.auth.model.Papel;
import com.academia.bjj.auth.model.PapelNome;
import com.academia.bjj.auth.model.Usuario;
import com.academia.bjj.auth.repository.PapelRepository;
import com.academia.bjj.auth.repository.PasswordResetTokenRepository;
import com.academia.bjj.auth.repository.RefreshTokenRepository;
import com.academia.bjj.auth.repository.UsuarioRepository;
import com.academia.bjj.auth.security.JwtService;
import com.academia.bjj.auth.service.impl.AuthServiceImpl;
import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.common.exception.ConflictException;
import com.academia.bjj.config.AppProperties;
import com.academia.bjj.notificacao.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios das regras criticas de autenticacao (diretriz 11):
 * conflito de e-mail no registro e bloqueio por tentativas de login (RF-101).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock UsuarioRepository usuarioRepository;
    @Mock PapelRepository papelRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock UsuarioMapper usuarioMapper;
    @Mock NotificationService notificationService;

    AppProperties props;
    AuthServiceImpl service;

    @BeforeEach
    void setup() {
        props = new AppProperties();
        props.getSecurity().setMaxLoginAttempts(3);
        props.getSecurity().setLockoutMinutes(15);
        service = new AuthServiceImpl(usuarioRepository, papelRepository, refreshTokenRepository,
                passwordResetTokenRepository, passwordEncoder, jwtService, usuarioMapper,
                notificationService, props);
    }

    @Test
    void register_deveLancarConflito_quandoEmailJaExiste() {
        when(usuarioRepository.existsByEmailIgnoreCase("dup@bjj.local")).thenReturn(true);

        assertThatThrownBy(() -> service.register(
                new RegisterRequest("Dup", "dup@bjj.local", "Senha@123")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void register_deveAtribuirPapelAluno_eEmitirTokens() {
        when(usuarioRepository.existsByEmailIgnoreCase(any())).thenReturn(false);
        when(papelRepository.findByNome(PapelNome.ALUNO))
                .thenReturn(Optional.of(new Papel(PapelNome.ALUNO, "aluno")));
        when(passwordEncoder.encode(any())).thenReturn("$2a$hash");
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access.jwt.token");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioMapper.toResponse(any()))
                .thenReturn(new UsuarioResponse(1L, "Novo", "novo@bjj.local", java.util.List.of("ALUNO")));

        var resp = service.register(new RegisterRequest("Novo", "novo@bjj.local", "Senha@123"));

        assertThat(resp.accessToken()).isNotNull();
        assertThat(resp.refreshToken()).isNotNull();
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, atLeastOnce()).save(captor.capture());
        assertThat(captor.getValue().getPapeis())
                .extracting(p -> p.getNome())
                .containsExactly(PapelNome.ALUNO);
    }

    @Test
    void login_deveBloquearConta_aposExcederTentativas() {
        Usuario usuario = new Usuario();
        usuario.setEmail("alvo@bjj.local");
        usuario.setSenhaHash("$2a$hash");
        usuario.setAtivo(true);
        usuario.setPapeis(Set.of(new Papel(PapelNome.ALUNO, "aluno")));

        when(usuarioRepository.findByEmailIgnoreCase("alvo@bjj.local")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        lenient().when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoginRequest req = new LoginRequest("alvo@bjj.local", "errada");

        // maxLoginAttempts = 3: as 3 tentativas falham por credencial e a 3a aciona o bloqueio.
        for (int i = 0; i < 3; i++) {
            assertThatThrownBy(() -> service.login(req)).isInstanceOf(BusinessException.class);
        }

        assertThat(usuario.getBloqueadoAte()).isNotNull();
        assertThat(usuario.getBloqueadoAte()).isAfter(java.time.OffsetDateTime.now());

        // Proxima tentativa e barrada pelo bloqueio (mesmo que a senha estivesse certa).
        assertThatThrownBy(() -> service.login(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("bloqueada");
    }
}
