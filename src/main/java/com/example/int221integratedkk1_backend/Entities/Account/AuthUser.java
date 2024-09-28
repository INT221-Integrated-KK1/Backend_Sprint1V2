package com.example.int221integratedkk1_backend.Entities.Account;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class AuthUser extends User implements Serializable {
    private String oid;
    private String email;
    private Role role;
    private String name;

    public AuthUser() {
        super("anonymous", "", new ArrayList<GrantedAuthority>());
    }

    public AuthUser(String userName, String password, Collection<? extends GrantedAuthority> authorities,
                    String oid, String email, Role role, String name) {
        super(userName, password, authorities);
        this.oid = oid;
        this.email = email;
        this.role = role;
        this.name = name;
    }

}

