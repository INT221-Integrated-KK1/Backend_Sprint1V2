package com.example.int221integratedkk1_backend.Services.Account;

import com.example.int221integratedkk1_backend.Entities.Account.AuthUser;
import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
import com.example.int221integratedkk1_backend.Exception.UnauthorizedException;
import com.example.int221integratedkk1_backend.Repositories.Account.UserRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UsersEntity users = userRepository.findByUsername(userName);
        if (users == null) {
            throw new UnauthorizedException(userName + " does not exist !!");
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = () -> users.getRole().toString();
        roles.add(grantedAuthority);
        roles.add(new SimpleGrantedAuthority("PUBLIC"));
        return new AuthUser(users.getUsername(), users.getPassword(), roles,
                users.getOid(), users.getEmail(), users.getRole(), users.getName());
    }


    public UserDetails loadUserByOid(String oid) throws UsernameNotFoundException {
        UsersEntity users = userRepository.findByOid(oid);
        if (users == null) {
            throw new UnauthorizedException(oid + " oid does not exist");
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = () -> users.getRole().toString();
        roles.add(grantedAuthority);

        return new AuthUser(users.getUsername(), users.getPassword(), roles,
                users.getOid(), users.getEmail(), users.getRole(),users.getName());
    }

    @Transactional
    public boolean authenticateUser(String username, String password) {
        UsersEntity users = userRepository.findByUsername(username);
        if (users == null) {
            return false;
        } else {
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 32);
            char[] passwordArray = password.toCharArray();
            return argon2.verify(users.getPassword(), passwordArray);
        }
    }
}
