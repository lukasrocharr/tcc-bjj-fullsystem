package com.academia.bjj.auth.security;

import com.academia.bjj.auth.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapta {@link Usuario} para o contrato {@link UserDetails} do Spring Security.
 */
public class AppUserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String senhaHash;
    private final boolean ativo;
    private final boolean naoBloqueado;
    private final Set<GrantedAuthority> authorities;

    public AppUserPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.senhaHash = usuario.getSenhaHash();
        this.ativo = usuario.isAtivo();
        this.naoBloqueado = usuario.getBloqueadoAte() == null
                || usuario.getBloqueadoAte().isBefore(OffsetDateTime.now());
        this.authorities = usuario.getPapeis().stream()
                .map(p -> new SimpleGrantedAuthority(p.getNome().authority()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return naoBloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
