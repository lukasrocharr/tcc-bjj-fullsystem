package com.academia.bjj.auth.security;

import com.academia.bjj.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Geracao e validacao de JWT de acesso (RF-097). O refresh token e opaco e
 * persistido (ver {@link com.academia.bjj.auth.model.RefreshToken}).
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpMinutes;
    private final String issuer;

    public JwtService(AppProperties props) {
        byte[] keyBytes = props.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpMinutes = props.getJwt().getAccessTokenExpirationMinutes();
        this.issuer = props.getJwt().getIssuer();
    }

    public String generateAccessToken(Long usuarioId, String email, List<String> authorities) {
        Instant now = Instant.now();
        Instant exp = now.plus(accessExpMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(usuarioId))
                .claim("email", email)
                .claim("roles", authorities)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
