package com.academia.bjj.common.exception;

/** Violacao de regra de negocio (HTTP 422 ou 400). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
