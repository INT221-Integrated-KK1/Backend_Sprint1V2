package com.example.int221integratedkk1_backend.Repositories.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<StatusEntity, Integer> {
    Optional<StatusEntity> findByName(String name);
}
