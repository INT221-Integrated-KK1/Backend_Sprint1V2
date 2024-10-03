package com.example.int221integratedkk1_backend.Entities.Account;

import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users", schema = "itbkk_shared")
public class UsersEntity {
//    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "oid")
    private String oid;
    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "role")
    private Role role;

}

