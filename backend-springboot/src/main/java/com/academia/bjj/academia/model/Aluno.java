package com.academia.bjj.academia.model;

import com.academia.bjj.auth.model.Usuario;
import com.academia.bjj.common.model.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Aluno da academia (RF-041). Dados pessoais e de saude. O vinculo com
 * {@link Usuario} (login do portal do aluno) e opcional. A faixa atual e
 * mantida como campo desnormalizado e atualizada pelo modulo de graduacao (Fase 3).
 */
@Entity
@Table(name = "aluno")
public class Aluno extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(length = 30)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 14)
    private String cpf;

    @Column(name = "contato_emergencia", length = 120)
    private String contatoEmergencia;

    @Column(name = "observacoes_saude", length = 1000)
    private String observacoesSaude;

    @Column(name = "faixa_atual", length = 40)
    private String faixaAtual;

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
    }

    public String getObservacoesSaude() {
        return observacoesSaude;
    }

    public void setObservacoesSaude(String observacoesSaude) {
        this.observacoesSaude = observacoesSaude;
    }

    public String getFaixaAtual() {
        return faixaAtual;
    }

    public void setFaixaAtual(String faixaAtual) {
        this.faixaAtual = faixaAtual;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
