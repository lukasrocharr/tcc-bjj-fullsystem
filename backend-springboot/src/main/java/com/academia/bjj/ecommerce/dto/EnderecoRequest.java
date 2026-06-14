package com.academia.bjj.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
        @NotBlank(message = "O CEP e obrigatorio")
        @Size(max = 9)
        String cep,

        @NotBlank(message = "O logradouro e obrigatorio")
        @Size(max = 160)
        String logradouro,

        @NotBlank(message = "O numero e obrigatorio")
        @Size(max = 20)
        String numero,

        @Size(max = 80)
        String complemento,

        @Size(max = 80)
        String bairro,

        @NotBlank(message = "A cidade e obrigatoria")
        @Size(max = 80)
        String cidade,

        @NotBlank(message = "A UF e obrigatoria")
        @Size(min = 2, max = 2)
        String uf
) {
}
