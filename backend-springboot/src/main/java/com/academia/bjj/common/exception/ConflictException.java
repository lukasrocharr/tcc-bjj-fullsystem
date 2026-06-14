package com.academia.bjj.common.exception;

/** Conflito de estado (HTTP 409), ex.: e-mail ja cadastrado. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
