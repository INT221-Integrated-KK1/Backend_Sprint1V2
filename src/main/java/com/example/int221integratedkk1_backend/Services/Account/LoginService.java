//package com.example.int221integratedkk1_backend.Services.Account;
//
//import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
//import com.example.int221integratedkk1_backend.Repositories.Account.UserRepository;
//import de.mkammerer.argon2.Argon2;
//import de.mkammerer.argon2.Argon2Factory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class LoginService {
//    @Autowired
//    private final UserRepository userRepository;
//
//    public LoginService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public boolean authenticate(String username, String password) {
//        Optional<UsersEntity> user = Optional.ofNullable(userRepository.findByUsername(username));
//
//        if (user.isEmpty()) {
//            return false;
//        } else {
//            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 32);
//            char[] passwordArray = password.toCharArray();
//            return argon2.verify(user.get().getPassword(), passwordArray);
//        }
//    }
//}