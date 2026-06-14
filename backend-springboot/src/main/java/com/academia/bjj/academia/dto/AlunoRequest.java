package com.academia.bjj.academia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlunoRequest(
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120)
        String nome,

        @NotBlank(message = "O e-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        @Size(max = 180)
        String email,

        @Size(max = 30)
        String telefone,

        LocalDate dataNascimento,

        @Size(max = 14)
        String cpf,

        @Size(max = 120)
        String contatoEmergencia,

        @Size(max = 1000)
        String observacoesSaude,

        @Size(max = 40)
        String faixaAtual,

        Boolean ativo,

        Long usuarioId
) {
}
