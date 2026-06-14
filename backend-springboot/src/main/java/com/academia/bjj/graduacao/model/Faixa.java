package com.academia.bjj.graduacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Faixa configuravel (RF-068). 'ordem' define a progressao; 'grausMax' o
 * numero de graus antes da proxima faixa.
 */
@Entity
@Table(name = "faixa")
public class Faixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private CategoriaFaixa categoria;

    @Column(nullable = false)
    private int ordem;

    @Column(name = "graus_max", nullable = false)
    private int grausMax = 4;

    @Column(nullable = false)
    private boolean ativo = true;

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

    public CategoriaFaixa getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaFaixa categoria) {
        this.categoria = categoria;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getGrausMax() {
        return grausMax;
    }

    public void setGrausMax(int grausMax) {
        this.grausMax = grausMax;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
