package com.academia.bjj.frequencia.model;

/** Origem do registro de presenca (RF-056, RF-058). */
public enum OrigemCheckIn {
    /** Check-in feito pelo proprio aluno (self-service). */
    SELF,
    /** Presenca registrada pelo professor (chamada em lote). */
    PROFESSOR
}
