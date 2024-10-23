package com.example.int221integratedkk1_backend.Repositories.Account;

import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, String> {
    UsersEntity findByUsername(String username);

    UsersEntity findByOid(String oid);

}
