package com.example.int221integratedkk1_backend.DTOS;

import com.example.int221integratedkk1_backend.Entities.Taskboard.AccessRight;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollabRequest {
    private String email;
    private AccessRight accessRight;  // This represents the access level (READ or WRITE)

    // Constructor for adding collaborator
    public void CollaboratorRequest(String email, AccessRight accessRight) {
        this.email = email;
        this.accessRight = accessRight;
    }

    // Empty constructor if needed by frameworks (e.g., for deserialization)
    public void CollaboratorRequest() {}


}

