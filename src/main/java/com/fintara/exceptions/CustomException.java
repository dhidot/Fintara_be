package com.fintara.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class CustomException extends RuntimeException {
    private HttpStatus status;
    private List<String> errors;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomException(String message, HttpStatus status, List<String> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }
}

