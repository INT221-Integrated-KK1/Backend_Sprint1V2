package com.example.int221integratedkk1_backend.Repositories.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, String> {

    // Find all boards for a specific user by ownerId
    List<BoardEntity> findByOwnerId(String ownerId);

    // Find a board by ID and ownerId (for authorization)
    Optional<BoardEntity> findByIdAndOwnerId(String id, String ownerId);

    // Check if a user already owns a board (by ownerId)
    boolean existsByOwnerId(String ownerId);
}
