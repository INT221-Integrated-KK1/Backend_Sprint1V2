package com.example.int221integratedkk1_backend.DTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class JwtRequestUser {
    @NotBlank
    @Size(min = 1, max = 50)
    private String userName;
    @NotBlank
    @Size(min = 1, max = 14)
    private String password;
}
