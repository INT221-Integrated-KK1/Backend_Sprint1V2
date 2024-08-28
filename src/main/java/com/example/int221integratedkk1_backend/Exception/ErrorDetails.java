package com.example.int221integratedkk1_backend.Exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.*;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {
    private int status;
    private String message;
    private String instance;
    private List<ValidationError> errors;

    // Constructor for non-validation errors
    public ErrorDetails(int status, String message, String instance) {
        this.status = status;
        this.message = message;
        this.instance = instance;
    }
    // Inner class for validation errors
    @Data
    @RequiredArgsConstructor
    public static class ValidationError { // inner class
        private final String field;
        private final String message;
    }

    public void addValidationError(String field, String message){
        if(Objects.isNull(errors)){
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }
}