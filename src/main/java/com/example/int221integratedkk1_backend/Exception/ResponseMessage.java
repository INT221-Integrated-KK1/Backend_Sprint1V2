package com.example.int221integratedkk1_backend.Exception;

import io.jsonwebtoken.Claims;

public class ResponseMessage {
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setClaims(Claims claims) {
    }

    public void setToken(String token) {
    }
}
