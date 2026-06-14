package com.academia.bjj.ecommerce.model;

import com.academia.bjj.common.model.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Variacao de produto (tamanho/cor) que carrega o estoque (RF-012).
 * Preco efetivo = produto.preco + precoAdicional.
 */
@Entity
@Table(name = "variacao_produto")
public class VariacaoProduto extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false, unique = true, length = 60)
    private String sku;

    @Column(length = 20)
    private String tamanho;

    @Column(length = 30)
    private String cor;

    @Column(name = "preco_adicional", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoAdicional = BigDecimal.ZERO;

    @Column(nullable = false)
    private int estoque;

    public BigDecimal getPrecoEfetivo() {
        return produto.getPreco().add(precoAdicional);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public BigDecimal getPrecoAdicional() {
        return precoAdicional;
    }

    public void setPrecoAdicional(BigDecimal precoAdicional) {
        this.precoAdicional = precoAdicional;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
}
