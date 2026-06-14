package com.academia.bjj.financeiro.model;

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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Registro de uma tentativa/efetivacao de pagamento de mensalidade (RF-076).
 */
@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "mensalidade_id", nullable = false)
    private Mensalidade mensalidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusPagamento status;

    @Column(name = "gateway_id", length = 80)
    private String gatewayId;

    @Column(name = "data_hora", nullable = false)
    private OffsetDateTime dataHora;

    public Pagamento() {
    }

    public Pagamento(Mensalidade mensalidade, BigDecimal valor, MetodoPagamento metodo,
                     StatusPagamento status, String gatewayId, OffsetDateTime dataHora) {
        this.mensalidade = mensalidade;
        this.valor = valor;
        this.metodo = metodo;
        this.status = status;
        this.gatewayId = gatewayId;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public Mensalidade getMensalidade() {
        return mensalidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public MetodoPagamento getMetodo() {
        return metodo;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }
}
