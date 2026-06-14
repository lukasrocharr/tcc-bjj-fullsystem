package com.academia.bjj.academia.dto;

import com.academia.bjj.academia.model.DiaSemana;

import java.time.LocalTime;

/** Referencia compacta de turma para uso aninhado em respostas. */
public record TurmaRef(Long id, String nome, DiaSemana diaSemana,
                       LocalTime horaInicio, LocalTime horaFim) {
}
