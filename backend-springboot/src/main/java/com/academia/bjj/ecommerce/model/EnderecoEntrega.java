package com.academia.bjj.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Endereco de entrega gravado como snapshot no pedido (RF-022). Mantido no
 * proprio pedido para preservar o historico independente de cadastros futuros.
 */
@Embeddable
public class EnderecoEntrega {

    @Column(name = "end_cep", length = 9)
    private String cep;

    @Column(name = "end_logradouro", length = 160)
    private String logradouro;

    @Column(name = "end_numero", length = 20)
    private String numero;

    @Column(name = "end_complemento", length = 80)
    private String complemento;

    @Column(name = "end_bairro", length = 80)
    private String bairro;

    @Column(name = "end_cidade", length = 80)
    private String cidade;

    @Column(name = "end_uf", length = 2)
    private String uf;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
