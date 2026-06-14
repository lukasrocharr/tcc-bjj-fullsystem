package com.academia.bjj.academia.controller;

import com.academia.bjj.academia.dto.ModalidadeRequest;
import com.academia.bjj.academia.dto.ModalidadeResponse;
import com.academia.bjj.academia.service.ModalidadeService;
import com.academia.bjj.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/modalidades")
@Tag(name = "Modalidades", description = "Gestao de modalidades (RF-044)")
public class ModalidadeController {

    private static final String ADMIN = "hasAnyRole('ADMIN','SUPER_ADMIN')";

    private final ModalidadeService service;

    public ModalidadeController(ModalidadeService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<ModalidadeResponse> listar(
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return service.listar(ativo, pageable);
    }

    @GetMapping("/{id}")
    public ModalidadeResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @PreAuthorize(ADMIN)
    public ResponseEntity<ModalidadeResponse> criar(@Valid @RequestBody ModalidadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN)
    public ModalidadeResponse atualizar(@PathVariable Long id, @Valid @RequestBody ModalidadeRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
