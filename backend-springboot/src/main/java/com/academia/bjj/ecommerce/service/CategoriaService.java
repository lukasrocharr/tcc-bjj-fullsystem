package com.academia.bjj.ecommerce.service;

import com.academia.bjj.ecommerce.dto.CategoriaRequest;
import com.academia.bjj.ecommerce.dto.CategoriaResponse;

import java.util.List;

public interface CategoriaService {
    CategoriaResponse criar(CategoriaRequest request);

    CategoriaResponse atualizar(Long id, CategoriaRequest request);

    List<CategoriaResponse> listar();

    void remover(Long id);
}
