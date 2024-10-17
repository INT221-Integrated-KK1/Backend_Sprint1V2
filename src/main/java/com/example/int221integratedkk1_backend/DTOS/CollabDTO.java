package com.example.int221integratedkk1_backend.DTOS;

import com.example.int221integratedkk1_backend.Entities.Taskboard.AccessRight;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CollabDTO {
    private String oid;
    private String name;
    private String email;
    private AccessRight accessRight;
    private Timestamp addedOn;

    public void CollaboratorDTO() {}

    public void CollaboratorDTO(String oid, String name, String email, AccessRight accessRight, Timestamp addedOn) {
        this.oid = oid;
        this.name = name;
        this.email = email;
        this.accessRight = accessRight;
        this.addedOn = addedOn;
    }
}