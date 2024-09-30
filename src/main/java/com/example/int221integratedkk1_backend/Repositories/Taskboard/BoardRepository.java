package com.example.int221integratedkk1_backend.Repositories.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, String> {

    List<BoardEntity> findByOwnerId(String ownerId);

    Optional<BoardEntity> findByIdAndOwnerId(String id, String ownerId);

    boolean existsByOwnerId(String ownerId);
}
