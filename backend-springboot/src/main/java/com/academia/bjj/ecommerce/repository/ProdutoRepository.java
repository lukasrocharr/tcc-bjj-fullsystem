package com.academia.bjj.ecommerce.repository;

import com.academia.bjj.ecommerce.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("""
            select p from Produto p
            where (:categoriaId is null or p.categoria.id = :categoriaId)
              and (:busca is null or lower(p.nome) like lower(concat('%', :busca, '%')))
              and (:apenasAtivos = false or p.ativo = true)
            """)
    Page<Produto> buscar(@Param("categoriaId") Long categoriaId,
                         @Param("busca") String busca,
                         @Param("apenasAtivos") boolean apenasAtivos,
                         Pageable pageable);
}
