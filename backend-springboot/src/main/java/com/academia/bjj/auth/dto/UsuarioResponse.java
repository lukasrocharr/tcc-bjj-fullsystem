package com.academia.bjj.auth.dto;

import java.util.List;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        List<String> papeis
) {
}
