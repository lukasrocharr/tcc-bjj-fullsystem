package com.academia.bjj.common.exception;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Payload padronizado de erro retornado pela API (diretriz 4 do prompt mestre).
 */
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorItem> errors
) {
    public record FieldErrorItem(String field, String message) {
    }

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(OffsetDateTime.now(), status, error, message, path, null);
    }

    public static ApiError of(int status, String error, String message, String path, List<FieldErrorItem> errors) {
        return new ApiError(OffsetDateTime.now(), status, error, message, path, errors);
    }
}
