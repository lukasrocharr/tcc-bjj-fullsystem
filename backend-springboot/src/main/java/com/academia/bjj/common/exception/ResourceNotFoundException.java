package com.academia.bjj.common.exception;

/** Recurso nao encontrado (HTTP 404). */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
