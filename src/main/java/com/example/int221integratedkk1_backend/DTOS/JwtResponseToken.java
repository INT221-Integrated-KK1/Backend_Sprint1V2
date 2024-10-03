package com.example.int221integratedkk1_backend.DTOS;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponseToken {
    private String access_token;
    private String refresh_token;

    public JwtResponseToken(String newAccessToken) {
        this.access_token = newAccessToken;
    }
}