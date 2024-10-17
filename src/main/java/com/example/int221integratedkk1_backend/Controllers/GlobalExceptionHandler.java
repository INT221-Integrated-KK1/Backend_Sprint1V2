package com.example.int221integratedkk1_backend.Controllers;

import com.example.int221integratedkk1_backend.Exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(ItemNotFoundException exception, WebRequest request) {
        return createErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(ValidateInputException.class)
    public ResponseEntity<String> handleValidateInputException(ValidateInputException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EmptyRequestBodyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEmptyRequestBodyException(EmptyRequestBodyException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(UnManageStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleUnManageStatusException(UnManageStatusException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(DuplicateStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleDuplicateStatusException(DuplicateStatusException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(DuplicateBoardException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleDuplicateBoardException(DuplicateBoardException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(InvalidTransferIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleInvalidTransferIdException(InvalidTransferIdException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), "Validation error. Check 'errors' field for details.", request.getDescription(false));
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorDetails.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Username or Password is incorrect.", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    @ExceptionHandler(CollaboratorAlreadyExistsException.class)
    public ResponseEntity<Object> handleCollaboratorAlreadyExistsException(CollaboratorAlreadyExistsException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT); // 409 Conflict
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message, request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private ResponseEntity<ErrorDetails> createErrorResponse(String message, HttpStatus httpStatus, WebRequest request, List<ErrorDetails.ValidationError> errors) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setStatus(httpStatus.value());
        errorDetails.setMessage(message);
        errorDetails.setInstance(request.getDescription(false));
        errorDetails.setErrors(errors);
        return ResponseEntity.status(httpStatus).body(errorDetails);
    }

}


