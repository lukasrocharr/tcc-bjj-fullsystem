package com.academia.bjj.auditoria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Registro de auditoria de uma acao sensivel (mutacao autenticada) (RF-095).
 */
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_email", length = 180)
    private String usuarioEmail;

    @Column(nullable = false, length = 10)
    private String metodo;

    @Column(nullable = false, length = 300)
    private String caminho;

    @Column(nullable = false)
    private int status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public Auditoria() {
    }

    public Auditoria(Long usuarioId, String usuarioEmail, String metodo, String caminho, int status) {
        this.usuarioId = usuarioId;
        this.usuarioEmail = usuarioEmail;
        this.metodo = metodo;
        this.caminho = caminho;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getCaminho() {
        return caminho;
    }

    public int getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
