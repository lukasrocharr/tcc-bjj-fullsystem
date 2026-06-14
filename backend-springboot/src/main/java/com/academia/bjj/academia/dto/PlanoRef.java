package com.academia.bjj.academia.dto;

import java.math.BigDecimal;

/** Referencia compacta de plano para uso aninhado em respostas. */
public record PlanoRef(Long id, String nome, BigDecimal valor) {
}
