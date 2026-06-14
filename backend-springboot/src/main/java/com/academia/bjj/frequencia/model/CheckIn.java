package com.academia.bjj.frequencia.model;

import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.model.Turma;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Registro de presenca de um aluno em uma turma num dia (RF-056 a RF-058).
 * Unico por (aluno, turma, data) para evitar duplicidade.
 */
@Entity
@Table(name = "check_in")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "data_hora", nullable = false)
    private OffsetDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private OrigemCheckIn origem;

    public CheckIn() {
    }

    public CheckIn(Aluno aluno, Turma turma, LocalDate data, OffsetDateTime dataHora, OrigemCheckIn origem) {
        this.aluno = aluno;
        this.turma = turma;
        this.data = data;
        this.dataHora = dataHora;
        this.origem = origem;
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Turma getTurma() {
        return turma;
    }

    public LocalDate getData() {
        return data;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }

    public OrigemCheckIn getOrigem() {
        return origem;
    }
}
