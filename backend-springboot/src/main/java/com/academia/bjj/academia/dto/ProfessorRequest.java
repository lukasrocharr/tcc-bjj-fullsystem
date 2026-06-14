package com.academia.bjj.academia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfessorRequest(
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120)
        String nome,

        @NotBlank(message = "O e-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        @Size(max = 180)
        String email,

        @Size(max = 30)
        String telefone,

        @Size(max = 40)
        String faixa,

        @Size(max = 1000)
        String bio,

        Boolean ativo,

        /** Vincula opcionalmente a um usuario de login existente. */
        Long usuarioId
) {
}
