package com.example.int221integratedkk1_backend.DTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String userName;

    @NotNull
    @NotBlank
    @Size(max = 14)
    private String password;
}