package com.example.int221integratedkk1_backend.Exception;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;


@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String instance;
    public ErrorResponse(int status, String message, String instance) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.instance = instance;
    }
}