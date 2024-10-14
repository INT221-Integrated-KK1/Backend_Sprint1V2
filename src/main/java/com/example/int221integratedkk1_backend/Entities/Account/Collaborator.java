package com.example.int221integratedkk1_backend.Entities.Account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "Collaborator", schema = "ITB-KK-V3")
public class Collaborator {
    @Id
    @Column(name = "collabsId")
    private String collaboratorId;

    @Column(name = "collabsName")
    private String collaboratorName;

    @Column(name = "collabsEmail")
    private String collaboratorEmail;

    @Column(name = "boardId")
    private String boardId;

    @Column(name = "ownerId")
    private String ownerId;

    @Column(name = "accessLevel")

    @Enumerated(EnumType.STRING)
    private AccessRight accessLevel;
    @Column(name = "addedOn")
    private Timestamp addedOn;
}