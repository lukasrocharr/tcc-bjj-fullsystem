package com.academia.bjj.config;

import com.academia.bjj.auth.model.Papel;
import com.academia.bjj.auth.model.PapelNome;
import com.academia.bjj.auth.model.Usuario;
import com.academia.bjj.auth.repository.PapelRepository;
import com.academia.bjj.auth.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria usuarios de teste (um por papel) de forma idempotente, com senha
 * hasheada via BCrypt. Os papeis em si vem da migration V2 (dados de referencia).
 *
 * Credenciais padrao (somente para desenvolvimento) - ver README:
 *   admin@bjj.local      / Admin@123      (ADMIN)
 *   professor@bjj.local  / Professor@123  (PROFESSOR)
 *   aluno@bjj.local      / Aluno@123       (ALUNO)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PapelRepository papelRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        criarSeNaoExistir("Administrador", "admin@bjj.local", "Admin@123",
                PapelNome.ADMIN, PapelNome.SUPER_ADMIN);
        criarSeNaoExistir("Professor Exemplo", "professor@bjj.local", "Professor@123",
                PapelNome.PROFESSOR);
        criarSeNaoExistir("Aluno Exemplo", "aluno@bjj.local", "Aluno@123",
                PapelNome.ALUNO);
    }

    private void criarSeNaoExistir(String nome, String email, String senha, PapelNome... papeis) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        for (PapelNome papelNome : papeis) {
            Papel papel = papelRepository.findByNome(papelNome)
                    .orElseThrow(() -> new IllegalStateException("Papel ausente no seed: " + papelNome));
            usuario.addPapel(papel);
        }
        usuarioRepository.save(usuario);
        log.info("Usuario de teste criado: {} ({})", email, String.join(",",
                java.util.Arrays.stream(papeis).map(Enum::name).toList()));
    }
}
