package com.academia.bjj.config;

import com.academia.bjj.auth.security.AppUserDetailsService;
import com.academia.bjj.auth.security.JwtAuthenticationFilter;
import com.academia.bjj.common.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuracao central de seguranca (diretriz 5): JWT stateless, RBAC via
 * {@code @PreAuthorize}, CORS para o frontend Angular e endpoints publicos.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Endpoints publicos. Observe que /api/v1/auth/me NAO esta aqui: exige
    // autenticacao (retorna os dados do usuario logado).
    private static final String[] PUBLIC_POST_ENDPOINTS = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            // Loja: checkout e webhook acessiveis a visitantes (RF-018, RF-030).
            "/api/v1/loja/checkout",
            "/api/v1/loja/webhook/**"
    };

    // Carrinho do visitante usa qualquer metodo HTTP sem login (X-Session-Id).
    private static final String[] PUBLIC_ANY_ENDPOINTS = {
            "/api/v1/loja/carrinho/**"
    };

    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/public/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health"
    };

    // Catalogo publico que alimenta a landing page (RF-001 a RF-010): apenas
    // leitura (GET). Escritas continuam protegidas por @PreAuthorize nos controllers.
    private static final String[] PUBLIC_CATALOG_GET = {
            "/api/v1/modalidades/**",
            "/api/v1/planos/**",
            "/api/v1/turmas/**",
            "/api/v1/professores/**",
            "/api/v1/graduacoes/faixas",
            "/api/v1/loja/categorias",
            "/api/v1/loja/produtos/**"
    };

    private final AppProperties props;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;
    private final com.academia.bjj.auditoria.repository.AuditoriaRepository auditoriaRepository;

    public SecurityConfig(AppProperties props,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          ObjectMapper objectMapper,
                          com.academia.bjj.auditoria.repository.AuditoriaRepository auditoriaRepository) {
        this.props = props;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, PUBLIC_CATALOG_GET).permitAll()
                        .requestMatchers(PUBLIC_ANY_ENDPOINTS).permitAll()
                        .requestMatchers(PUBLIC_GET_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) ->
                                writeError(res, HttpStatus.UNAUTHORIZED, "Nao autenticado", req.getRequestURI()))
                        .accessDeniedHandler((req, res, e) ->
                                writeError(res, HttpStatus.FORBIDDEN, "Acesso negado", req.getRequestURI())))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new com.academia.bjj.auditoria.AuditoriaFilter(auditoriaRepository),
                        JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(AppUserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(props.getCors().getAllowedOrigins().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private void writeError(HttpServletResponse res, HttpStatus status, String message, String path)
            throws java.io.IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), message, path);
        objectMapper.writeValue(res.getWriter(), body);
    }
}
