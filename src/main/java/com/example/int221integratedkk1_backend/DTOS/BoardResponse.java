package com.example.int221integratedkk1_backend.DTOS;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse{
    private String id;
    private String name;
    private OwnerDTO owner;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerDTO {
        private String oid;
        private String name;
    }
}