package com.academia.bjj.common.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Endpoint publico de verificacao de saude (equivalente ao /api/health do
 * backend Node legado, util para o docker-compose healthcheck).
 */
@RestController
@RequestMapping("/api/v1/public")
@Tag(name = "Publico", description = "Endpoints publicos da aplicacao")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Verifica se a aplicacao esta no ar")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "timestamp", OffsetDateTime.now().toString());
    }
}
