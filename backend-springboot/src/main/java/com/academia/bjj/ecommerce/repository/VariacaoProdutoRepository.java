package com.academia.bjj.ecommerce.repository;

import com.academia.bjj.ecommerce.model.VariacaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariacaoProdutoRepository extends JpaRepository<VariacaoProduto, Long> {

    boolean existsBySku(String sku);

    List<VariacaoProduto> findByProdutoId(Long produtoId);
}
