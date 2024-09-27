package com.example.int221integratedkk1_backend.Exception;

public class EmptyRequestBodyException extends RuntimeException {
    public EmptyRequestBodyException(String message) {

        super(message);
    }
}