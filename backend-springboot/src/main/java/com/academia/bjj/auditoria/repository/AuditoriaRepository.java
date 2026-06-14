package com.academia.bjj.auditoria.repository;

import com.academia.bjj.auditoria.model.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    Page<Auditoria> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
