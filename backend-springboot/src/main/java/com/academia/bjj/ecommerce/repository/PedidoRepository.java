package com.academia.bjj.ecommerce.repository;

import com.academia.bjj.ecommerce.model.Pedido;
import com.academia.bjj.ecommerce.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumero(String numero);

    long countByStatus(StatusPedido status);

    @Query("""
            select coalesce(sum(p.total), 0) from Pedido p
            where p.status = com.academia.bjj.ecommerce.model.StatusPedido.PAGO
              and p.createdAt >= :inicio and p.createdAt < :fim
            """)
    java.math.BigDecimal somarPagosNoPeriodo(@Param("inicio") java.time.OffsetDateTime inicio,
                                             @Param("fim") java.time.OffsetDateTime fim);

    Page<Pedido> findByUsuarioIdOrderByIdDesc(Long usuarioId, Pageable pageable);

    @Query("""
            select p from Pedido p
            where (:status is null or p.status = :status)
            order by p.id desc
            """)
    Page<Pedido> buscar(@Param("status") StatusPedido status, Pageable pageable);
}
