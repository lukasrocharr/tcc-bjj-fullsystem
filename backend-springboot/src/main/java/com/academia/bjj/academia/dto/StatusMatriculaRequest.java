package com.academia.bjj.academia.dto;

import com.academia.bjj.academia.model.StatusMatricula;
import jakarta.validation.constraints.NotNull;

public record StatusMatriculaRequest(
        @NotNull(message = "O status e obrigatorio")
        StatusMatricula status
) {
}
