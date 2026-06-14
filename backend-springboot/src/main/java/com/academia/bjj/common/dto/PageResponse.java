package com.academia.bjj.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper de paginacao estavel para a API (diretriz 7), evitando expor
 * a estrutura interna do {@link Page} do Spring Data.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
