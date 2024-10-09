package com.example.int221integratedkk1_backend.DTOS;

import com.example.int221integratedkk1_backend.Entities.Account.Visibility;
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
    private String name;

    private Visibility visibility = Visibility.PRIVATE; // เพิ่มฟิลด์นี้

}
