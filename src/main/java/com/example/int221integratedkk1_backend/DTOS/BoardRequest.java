package com.example.int221integratedkk1_backend.DTOS;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequest {

    @NotEmpty(message = "Board name cannot be empty")
    @Size(max = 120, message = "Board name must be less than 120 characters")
    private String boardName;

}
